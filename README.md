# MD-PDF Converter

**A clean, efficient Android app that converts Markdown documents to PDF with beautiful typography and print-ready layouts.**

<div align="center">

![Android](https://img.shields.io/badge/Android-API%2024+-brightgreen.svg)
![Kotlin](https://img.shields.io/badge/Kotlin-2.0.21-blue.svg)
![License](https://img.shields.io/badge/License-MIT-yellow.svg)
![Compose](https://img.shields.io/badge/Jetpack%20Compose-2024.02.00-purple.svg)

</div>

## ✨ Features

- **Native PDF Rendering** - Uses Android's built-in `PdfDocument` API for optimal performance
- **Strict Typography** - Professional font sizes (H1: 14pt max, Body: 10pt) for consistent output
- **Smart Pagination** - Automatically handles page breaks while keeping content blocks intact
- **Custom Fonts** - Beautiful typography with Montserrat, Roboto, and JetBrains Mono
- **Flexible Output** - Save to Downloads by default or choose custom location
- **Multiple Formats** - Support for A3, A4, A5, and Letter page sizes
- **Responsive Margins** - Compact, Normal, and Wide margin options
- **Dark Mode Support** - Professional dark theme with comfortable colors
- **Material 3 Design** - Modern UI following Material Design guidelines
- **No Ads** - Clean, distraction-free experience

## 📱 Screenshots

| Convert Screen | Settings | Dark Mode | PDF Output |
|---|---|---|---|
| *File selection and conversion options* | *Page size and margin controls* | *Professional dark theme* | *Clean, print-ready PDFs* |

## 🚀 Quick Start

### Prerequisites

- Android Studio Hedgehog (2023.1.1) or later
- Android SDK 24+ (Android 7.0+)
- Kotlin 2.0.21+

### Building the App

1. **Clone the repository**
   ```bash
   git clone https://github.com/teocci/android-md-pdf.git
   cd android-md-pdf
   ```

2. **Build and run**
   ```bash
   ./gradlew assembleDebug
   ```

3. **Install on device**
   ```bash
   ./gradlew installDebug
   ```

## 🛠️ Development

### Project Structure

```
app/src/main/
├── assets/
│   ├── design_system.json      # Theme configuration
│   └── samples/sample.md       # Example markdown file
├── java/com/github/teocci/mdpdf/
│   ├── MainActivity.kt         # Main entry point
│   ├── domain/                 # Business logic
│   │   ├── ConvertMarkdownToPdf.kt
│   │   └── PdfPageSpec.kt
│   ├── navigation/             # App navigation
│   │   └── NavGraph.kt
│   ├── pdf/                    # PDF rendering engine
│   │   ├── Fonts.kt
│   │   ├── LayoutUtils.kt
│   │   └── PdfRenderer.kt
│   ├── theme/                  # Design system
│   │   ├── AppTheme.kt
│   │   └── DesignSystemLoader.kt
│   ├── ui/                     # User interface
│   │   ├── components/
│   │   ├── convert/
│   │   └── donate/
│   └── viewmodel/              # State management
│       └── ConvertViewModel.kt
└── res/font/                   # Google Fonts integration
```

### Key Technologies

- **UI Framework**: Jetpack Compose with Material 3
- **Architecture**: MVVM with StateFlow
- **Navigation**: Navigation Compose
- **Fonts**: Google Fonts (downloadable fonts)
- **Markdown Parser**: CommonMark
- **PDF Generation**: Native Android PDF APIs
- **Storage**: Storage Access Framework + MediaStore

### Build Commands

```bash
# Debug build
./gradlew assembleDebug

# Release build
./gradlew assembleRelease

# Run tests
./gradlew test

# Run instrumentation tests
./gradlew connectedAndroidTest

# Clean build
./gradlew clean

# Run linting
./gradlew lint
```

## 📋 Supported Markdown Features

| Feature | Support | Notes |
|---------|---------|-------|
| Headers (H1-H6) | ✅ | Capped at 14pt/12pt/11pt/10pt |
| **Bold** and *Italic* | ✅ | Proper font weights |
| `Inline Code` | ✅ | Monospaced font |
| Code Blocks | ✅ | Syntax highlighting, smart pagination |
| Lists (ordered/unordered) | ✅ | Proper indentation and nesting |
| Block Quotes | ✅ | Visual indicators |
| Links | ✅ | Underlined text |
| Horizontal Rules | ✅ | Clean line separators |
| Tables | ❌ | Not yet implemented |
| Images | ❌ | Not yet implemented |

## ⚙️ Configuration

### Design System

The app uses a JSON-based design system located at `app/src/main/assets/design_system.json`:

```json
{
  "colors": {
    "primary": "#F4A261",
    "surface": "#FFFFFF",
    ...
  },
  "typography": {
    "titleFont": "Montserrat",
    "bodyFont": "Roboto",
    "monoFont": "JetBrainsMono"
  },
  "spacing": { "xs": 4, "sm": 8, "md": 16, "lg": 24, "xl": 32 },
  "radius": { "sm": 8, "md": 12, "lg": 16 }
}
```

### Typography Rules

The app enforces strict typography limits for consistent, professional output:

- **H1**: Maximum 14pt, bold Montserrat
- **H2**: 12pt, bold Montserrat  
- **H3**: 11pt, bold Montserrat
- **H4**: 10pt, bold Montserrat
- **Body Text**: 10pt, Roboto
- **Code**: 10pt, JetBrains Mono

## 🤝 Contributing

We welcome contributions! Please see our [Contributing Guidelines](CONTRIBUTING.md) for details.

### Development Guidelines

1. **Code Style**: Follow Kotlin coding conventions
2. **Architecture**: Maintain MVVM pattern with clear separation of concerns
3. **Testing**: Write unit tests for business logic
4. **Commits**: Use conventional commit messages
5. **Pull Requests**: Include description and test results

### Reporting Issues

Please use our [Issue Template](.github/ISSUE_TEMPLATE.md) when reporting bugs or requesting features.

## 📄 License

This project is licensed under the MIT License - see the [LICENSE](LICENSE) file for details.

### Dependencies

All dependencies use permissive licenses:

- **AndroidX Libraries**: Apache 2.0
- **Jetpack Compose**: Apache 2.0
- **Material Design**: Apache 2.0
- **CommonMark**: BSD 2-Clause
- **Google Fonts**: SIL Open Font License / Apache 2.0

## 💖 Support

If you find this app useful, consider supporting its development:

- ☕ [Buy me a coffee](https://buymeacoffee.com/teocci)
- 🎯 [Patreon](https://www.patreon.com/teocci)

## 🔗 Links

- **Repository**: [github.com/teocci/android-md-pdf](https://github.com/teocci/android-md-pdf)
- **Issues**: [Bug Reports & Feature Requests](https://github.com/teocci/android-md-pdf/issues)
- **Documentation**: [Wiki](https://github.com/teocci/android-md-pdf/wiki)

## 📈 Roadmap

- [ ] Table support in Markdown
- [ ] Image embedding in PDFs
- [ ] Custom CSS styling
- [ ] Batch conversion
- [ ] Cloud storage integration
- [ ] Export to other formats (DOCX, HTML)

---

<div align="center">
Made with ❤️ by <a href="https://github.com/teocci">Teocci</a>
</div>