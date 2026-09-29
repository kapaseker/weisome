# Cyan theme source

- Source: <https://github.com/ChanningHan/juejin-markdown-theme-channing-cyan>
- Pinned commit: `c843c2f`
- Upstream file: `channing-cyan.scss`
- Upstream theme license: MIT
- Registered in juejin-markdown-themes as `channing-cyan`; WeiSome renames it `cyan`.

`channing-cyan.scss` is an unmodified source snapshot retained for local
comparison and traceability. It is not included in WeiSome application
resources or packages.

WeiSome adapts only article-content rules to Compose preview styles and inline
HTML export. The `.markdown-body` checkered grid background is not rendered:
the export is a block fragment with no body wrapper. The h1 background PNG
glow, the animated h1 gradient shadow, and the h3 width/rotation animations are
dynamic effects the export cannot express (h3 decorations render in their
final static state). The `figcaption` rules and `strong` 「」 marks have no
model element. Code-block colors continue to come from WeiSome's selected code
theme.
