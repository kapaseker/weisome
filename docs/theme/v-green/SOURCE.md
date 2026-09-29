# V-Green theme source

- Source: <https://github.com/DawnLck/juejin-markdown-theme-v-green>
- Pinned commit: `015f88b`
- Upstream file: `v-green.scss`
- Upstream theme license: MIT
- Registered in juejin-markdown-themes as `v-green`; WeiSome names it `V-Green`.

`v-green.scss` is an unmodified source snapshot retained for local comparison
and traceability. It is not included in WeiSome application resources or
packages.

WeiSome adapts only article-content rules to Compose preview styles and inline
HTML export. The `heading:first-child` top-margin collapse and the green
ordered-list `::marker` are selector-based effects the inline-CSS model cannot
express. The unordered list's green `•` bullets are drawn via `li::before`;
the model has no list-item content hook. The link `⇲` glyph renders after the
anchor text (the model appends icons; upstream places it before). The
`details`/`summary` rules have no model element. Code-block colors continue to
come from WeiSome's selected code theme.
