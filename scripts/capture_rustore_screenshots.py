#!/usr/bin/env python3
"""Скриншоты витрины RuStore: 1080×1920, 9:16, без статус-бара и навигации Android."""
from __future__ import annotations

import os
import re
import shutil
import subprocess
import sys
import time
import xml.etree.ElementTree as ET
from pathlib import Path

ROOT = Path(__file__).resolve().parents[1]
OUT = ROOT / "docs" / "store-screenshots"
SHOT_W, SHOT_H = 1080, 1920
PKG = "ru.akarakuts.porog"
ACT = f"{PKG}/.MainActivity"
UID = Path("/tmp/porog_uid.xml")
ADB = os.environ.get("ADB", "adb")
CREAM = (0xF4, 0xED, 0xE3)
RUSTORE = Path("/Users/a.karakuts/Documents/Bars/rustore/porog/screenshots")
READY = Path("/Users/a.karakuts/Documents/Bars/rustore/porog/rustore-upload-ready/screenshots")


def adb(*args: str, check: bool = True) -> subprocess.CompletedProcess:
    return subprocess.run([ADB, *args], check=check, capture_output=True)


def dump_root() -> ET.Element:
    adb("shell", "uiautomator", "dump", "/sdcard/uid.xml")
    adb("pull", "/sdcard/uid.xml", str(UID))
    return ET.parse(UID).getroot()


def nodes() -> list[ET.Element]:
    return list(dump_root().iter("node"))


def bounds(n: ET.Element) -> tuple[int, int, int, int] | None:
    m = re.match(r"\[(\d+),(\d+)\]\[(\d+),(\d+)\]", n.attrib.get("bounds", ""))
    if not m:
        return None
    return tuple(int(x) for x in m.groups())  # type: ignore[return-value]


def tap_xy(x: int, y: int) -> None:
    adb("shell", "input", "tap", str(x), str(y))


def tap_center(n: ET.Element) -> bool:
    b = bounds(n)
    if not b:
        return False
    tap_xy((b[0] + b[2]) // 2, (b[1] + b[3]) // 2)
    return True


def find_node(pred) -> ET.Element | None:
    for n in nodes():
        if pred(n):
            return n
    return None


def wait_pred(pred, tries: int = 20) -> ET.Element | None:
    for _ in range(tries):
        n = find_node(pred)
        if n is not None:
            return n
        time.sleep(0.35)
    return None


def text_is(*vals: str):
    return lambda n: n.attrib.get("text") in vals


def desc_is(*vals: str):
    return lambda n: n.attrib.get("content-desc") in vals


def tap_text(*labels: str) -> None:
    n = wait_pred(lambda x: x.attrib.get("text") in labels or x.attrib.get("content-desc") in labels)
    if n is None or not tap_center(n):
        raise SystemExit(f"не найден: {labels}")
    time.sleep(0.8)


def cap(path: Path) -> None:
    adb("shell", "input", "keyevent", "224", check=False)
    time.sleep(0.2)
    raw = adb("exec-out", "screencap", "-p")
    path.write_bytes(raw.stdout)


def set_store_display() -> None:
    adb("shell", "settings", "put", "global", "policy_control", "immersive.full=*")
    adb("shell", "wm", "size", f"{SHOT_W}x{SHOT_H}")
    time.sleep(1.6)


def restore_display() -> None:
    adb("shell", "settings", "delete", "global", "policy_control", check=False)
    adb("shell", "wm", "size", "reset", check=False)


def publish_store(src: Path, dst: Path) -> None:
    from PIL import Image

    im = Image.open(src).convert("RGB")
    if im.size != (SHOT_W, SHOT_H):
        im = im.resize((SHOT_W, SHOT_H), Image.Resampling.LANCZOS)
    # Status bar ~y0–48, gesture pill ~y1884–1892 на 1080×1920.
    top, bot = 52, 44
    body = im.crop((0, top, SHOT_W, SHOT_H - bot))
    out = body.resize((SHOT_W, SHOT_H), Image.Resampling.LANCZOS)
    dst.parent.mkdir(parents=True, exist_ok=True)
    out.save(dst, "PNG", optimize=True)


def swipe(x1: int, y1: int, x2: int, y2: int, ms: int = 400) -> None:
    adb("shell", "input", "swipe", str(x1), str(y1), str(x2), str(y2), str(ms))
    time.sleep(0.45)


def main() -> None:
    OUT.mkdir(parents=True, exist_ok=True)
    store = OUT / f"store_{SHOT_W}x{SHOT_H}"
    store.mkdir(parents=True, exist_ok=True)

    adb("shell", "input", "keyevent", "224", check=False)
    try:
        set_store_display()
        for perm in (
            "android.permission.ACCESS_FINE_LOCATION",
            "android.permission.ACCESS_COARSE_LOCATION",
            "android.permission.READ_CALENDAR",
            "android.permission.POST_NOTIFICATIONS",
        ):
            adb("shell", "pm", "grant", PKG, perm, check=False)
        adb("emu", "geo", "fix", "37.6173", "55.7558", check=False)
        adb("shell", "am", "force-stop", PKG, check=False)
        time.sleep(0.4)
        adb("shell", "am", "start", "-n", ACT)
        wait_pred(text_is("Порог", "Porog"))
        time.sleep(3.5)
        cap(OUT / "01_home.png")

        for label in ("Ключи", "Keys", "Кошелёк", "Wallet", "Пропуск", "Pass / badge"):
            n = find_node(text_is(label))
            if n is not None:
                tap_center(n)
                time.sleep(0.45)
        time.sleep(0.6)
        cap(OUT / "02_checklist.png")

        tap_text("Настройки", "Settings")
        time.sleep(0.8)
        swipe(540, 1700, 540, 280, 500)
        swipe(540, 1700, 540, 400, 400)
        time.sleep(0.5)
        cap(OUT / "03_settings.png")

        n = find_node(text_is("Помощь", "Help"))
        if n is None:
            swipe(540, 1750, 540, 200, 500)
            n = wait_pred(text_is("Помощь", "Help"))
        if n is None or not tap_center(n):
            raise SystemExit("нет кнопки Помощь")
        time.sleep(1.0)
        cap(OUT / "04_help.png")

        n = find_node(text_is("О программе", "About this app", "About"))
        if n is None:
            swipe(540, 1700, 540, 400, 400)
            n = wait_pred(text_is("О программе", "About this app"))
        if n is None or not tap_center(n):
            raise SystemExit("нет экрана О программе")
        time.sleep(1.0)
        cap(OUT / "05_about.png")
    finally:
        restore_display()

    for f in sorted(OUT.glob("0*.png")):
        publish_store(f, store / f.name)
        print("store", store / f.name, store.joinpath(f.name).stat().st_size)

    for dest in (RUSTORE, READY):
        dest.mkdir(parents=True, exist_ok=True)
        for f in sorted(store.glob("0*.png")):
            shutil.copy2(f, dest / f.name)
        print("copy", dest)


if __name__ == "__main__":
    try:
        main()
    except subprocess.CalledProcessError as e:
        sys.stderr.write((e.stderr or e.stdout or b"").decode() + "\n")
        raise
