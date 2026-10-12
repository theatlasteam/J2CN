# J2CN 0.1.0

ShadCN-style UI engine for Java ME (CLDC 1.1 / MIDP 2.0), Nokia 6300 target.
Retained-mode widgets on a MIDP Canvas, a clean grotesque bitmap font
(DejaVu Sans rendered to a 256x352 atlas, not the phone system font),
and CSS-like styling with cascade + specificity.

## Widgets

Label, Button (primary/secondary/outline/ghost/destructive), Input (native
TextBox editing), CheckBox, Switch, Slider, Progress, Badge, Separator,
Avatar (initials), Card, Row/Column layouts, Tabs, modal Dialog.

## Styling

```css
button { background: #18181b; color: #fafafa; radius: 6; padding: 8; }
.btn-outline { background: #ffffff; border-color: #e4e4e7; border-width: 1; }
#login { background: #18181b; color: #fafafa; }
```

Selectors: `tag`, `.class`, `#id`, `tag.class`. Specificity 1/10/100,
source order breaks ties. Properties: `color`, `background`,
`border-color`, `border-width`, `radius`, `padding`, `font-weight`,
`text-align`. Built-in `StyleSheet.DEFAULT_CSS` carries the ShadCN-like
defaults for both themes; append your own rules via `sheet().add(css)`.

Light and dark themes in `Theme` (`*` toggles in the showcase).

## Keys

Up/down moves focus, left/right + fire drive the focused widget,
1/7 page scroll. FIRE on an Input opens the native editor.

## Build / test

```sh
python3 tools/make_font.py   # regenerate bitmap font (Pillow needed)
./build.sh                   # dist/J2CN.jar + .jad, class version 47
./test/run.sh                # device build + desktop logic tests
```

`./test/run.sh` compiles/preverifies against the real device APIs and runs
18 desktop checks (theme tokens, color parsing, cascade/specificity,
box properties, layout math). Phone rendering, heap behavior and keypad
feel still need Nokia verification.

MIT — do what you want, no warranty.
