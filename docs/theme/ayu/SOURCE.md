# Ayu theme source

- Source: <https://github.com/imageslr/mweb-themes>
- Pinned commit: `a755840a128657dd63bd7284cf9999f778510209` (master HEAD, 2022-10-06)
- Upstream files: the `mweb-ayu.scss` import cascade under `src/themes/`
  (`mweb-ayu.scss` → `variables/bear-default.scss`, `bear-palettes/default.scss`,
  `bear-palettes/ayu.scss`, `bear-palettes/override.scss`, `core/mweb-bear.scss`,
  `prism-themes/default.scss`, `prism-themes/prism.scss`), snapshotted verbatim
  here preserving the relative directory structure
- Upstream theme license: the repository declares no license file at the pinned
  commit (no LICENSE/COPYING file exists)

The SCSS files are unmodified source snapshots retained for local comparison
and traceability. They are not included in WeiSome application resources or
packages. The compiled CSS only ships in upstream GitHub releases; the cascade
here fully determines the output.

WeiSome adapts only article-content rules to Compose preview styles and inline
HTML export. The export keeps the stylesheet's em units (the same convention
the pre-existing themes use) and converts the single rem value — the code
line-height 1.5rem — to 24px. Known deliberate deviations:

- Bear draws list bullets and ordered numbers in the accent color
  `#ff6a00` via `::before`/CSS counters; inline CSS cannot express
  pseudo-elements, so lists keep the standard markers.
- Code-block colors continue to come from WeiSome's selected code theme; the
  ayu Prism palette is not ported. Note that upstream
  `bear-palettes/override.scss` binds `$prism-color-char` and
  `$prism-color-attr-name` to `$prism-color-selector` (`#357F1E`), so the
  literal values in `bear-palettes/ayu.scss` are dead code upstream as well.
- html/body page-level rules (page background `#fafafa`, the 46rem container,
  the body font stack) are not applied to the exported block fragment; the
  headings keep their own `AvenirNext-Medium` font-family.
- The model has no `u` (underline gradient) or `mark` (highlighter) element,
  so those rules are not applicable.
- The code-block frame paints the selected code theme's background and carries
  the 1px `#bfc1c4` border on the `<pre>` frame itself (upstream puts the
  border on the inner `code` element), avoiding a transparent edge after the
  WeChat editor's paste sanitization.
