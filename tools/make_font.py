#!/usr/bin/env python3
"""Render the J2CN bitmap font: 256x352 atlas, 16x16 cells, 351 advances.

Glyph order matches UiFont: ASCII U+0020..U+007E, then Cyrillic U+0400..U+04FF.
Source is Inter Tight (SIL Open Font License 1.1, see tools/OFL.txt),
bundled as tools/InterTight.ttf. Requires Pillow.
"""
import argparse
from PIL import Image, ImageDraw, ImageFont

GLYPHS = [chr(c) for c in range(32, 127)] + [chr(c) for c in range(1024, 1280)]

p = argparse.ArgumentParser()
p.add_argument("--font", default="tools/InterTight.ttf")
p.add_argument("--out", default="assets/font.png")
p.add_argument("--metrics", default="assets/font.metrics")
p.add_argument("--size", type=int, default=13)
args = p.parse_args()

font = ImageFont.truetype(args.font, args.size)
atlas = Image.new("RGBA", (256, 352), (0, 0, 0, 0))
advances = bytearray()
for i, ch in enumerate(GLYPHS):
    tile = Image.new("RGBA", (16, 16), (0, 0, 0, 0))
    d = ImageDraw.Draw(tile)
    d.text((1, 0), ch, font=font, fill=(20, 20, 22, 255))
    atlas.alpha_composite(tile, ((i % 16) * 16, (i // 16) * 16))
    bbox = tile.getbbox()
    if bbox is None:
        adv = 6 if ch == " " else 8
    else:
        adv = bbox[2] + 1
        if adv < 5:
            adv = 5
        if adv > 16:
            adv = 16
    advances.append(adv)

atlas.save(args.out, optimize=True)
with open(args.metrics, "wb") as f:
    f.write(bytes(advances))
print(args.out, "256x352,", args.metrics, len(advances), "advances")
