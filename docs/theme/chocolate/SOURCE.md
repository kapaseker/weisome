# Chocolate theme source

- Source: <https://github.com/qklhk/juejin-markdown-theme-qklhk>
- Pinned commit: `4f2a290`
- Upstream file: `chocolate.scss`
- Upstream theme license: Apache

`chocolate.scss` is an unmodified source snapshot retained for local comparison
and traceability. It is not included in WeiSome application resources or
packages.

WeiSome adapts only article-content rules to Compose preview styles and inline
HTML export. The `.markdown-body` grid background is painted behind the Compose
preview and applied as inline gradients on an enclosing `<section>` in HTML export.
It retains the upstream 20px tiles and 5%-opacity brown lines without adding a base
color. Code blocks cover the grid with their selected code theme's opaque background.
WeChat paste/save retention still requires validation in the target editor.
The h5/h6 trailing numbered circles, the
strong `「」` brackets, and the em-strong highlight are pseudo-element effects
the inline-CSS model cannot express. Code-block colors continue to come from
WeiSome's selected code theme.
