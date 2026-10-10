# Claudette theme source

- Source: <https://github.com/CookPiu/typora-theme-claudette>
- Pinned commit: `0a6c75aa00b73898e765199c1398a1276a5eef7c`
- Upstream file: `claudette.css` (light variant; the dark `claudette-dark.css` is not used)
- Upstream theme license: MIT

`claudette.css` is an unmodified source snapshot retained for local comparison
and traceability. It is not included in WeiSome application resources or
packages.

WeiSome adapts only article-content rules to Compose preview styles and inline
HTML export. Typora-only rules (page background, paper sheet, sidebar, menus,
scrollbars, focus mode, callouts, YAML front matter, footnotes, TOC, task-list
checkboxes, print) are not applied. Code-block colors continue to come from
WeiSome's selected code theme.

Export values convert all em/rem units to px, rounding non-integer results to
the nearest even integer (halves round up); the Compose preview mirrors those
px values 1:1 as dp/sp. Known deliberate deviations are listed in
`.trae/documents/claudette-markdown-theme.md`.
