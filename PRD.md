# Note Wallpaper Maker

**Version:** 1.0  
**Platform:** Android 12+ (API 31+)  
**Document Type:** Product Requirements Document (PRD) + System Design Specification  
**Status:** Master Development Plan

---

# 1. Product Vision

## Overview

Note Wallpaper Maker is an offline Android application that allows users to transform any image into a personalized wallpaper by overlaying one or more note bubbles containing user-defined text.

The application does not generate images using AI. Instead, it performs deterministic image composition by combining:

- User-selected image
- Predefined bubble templates
- User-entered text

The resulting composition can be directly applied as the device wallpaper without requiring permanent storage.

---

# 2. Product Goals

## Primary Goals

- Create wallpaper-ready images from existing photos.
- Allow users to attach visual notes directly onto wallpapers.
- Provide instant wallpaper generation.
- Apply wallpapers with a single action.
- Operate entirely offline.

## Secondary Goals

- Support multiple bubble styles.
- Support export functionality.
- Support future customization features.

---

# 3. Target Users

### User Type A — Productivity Users

Users who want reminders directly visible on their home screen.

Examples:

- Shopping lists
- Daily reminders
- Important tasks
- Quotes

### User Type B — Personalization Users

Users who want customized wallpapers with messages.

Examples:

- Motivational quotes
- Personal notes
- Event countdowns

---

# 4. Product Scope

## Included

### Image Management

- Image selection
- Image decoding
- Image scaling
- Image cropping

### Note System

- Text input
- Bubble rendering
- Dynamic sizing

### Wallpaper System

- Wallpaper preview
- Wallpaper generation
- Wallpaper application

### Export System

- PNG export
- Local storage saving

---

## Excluded (Version 1)

- AI generation
- Cloud storage
- User accounts
- Online synchronization
- Video wallpapers
- Animated wallpapers
- Sticker marketplace
- Collaborative editing

---

# 5. Functional Requirements

---

## FR-001 Image Selection

### Description

Users must be able to select an image from device storage.

### Inputs

- Gallery image
- File manager image

### Supported Formats

- JPG
- JPEG
- PNG
- WEBP

### Outputs

```text
Android Uri
```

### Acceptance Criteria

- Image loads successfully.
- Unsupported files are rejected.
- Corrupted files produce error feedback.

---

## FR-002 Screen Resolution Detection

### Description

The application must detect the device's current display resolution.

### Inputs

```text
WindowMetrics
```

### Outputs

```text
Screen Width
Screen Height
```

### Acceptance Criteria

Generated wallpaper matches display resolution.

---

## FR-003 Wallpaper Fit Engine

### Description

The application must fit the selected image into wallpaper dimensions.

### Rules

#### Same Aspect Ratio

```text
Scale Only
```

#### Different Aspect Ratio

```text
Center Crop
```

#### Smaller Source

```text
Scale Up
```

#### Larger Source

```text
Scale Down
```

### Acceptance Criteria

No empty regions appear in the wallpaper.

---

## FR-004 Note Input

### Description

Users can enter text content.

### Constraints

Minimum:

```text
1 character
```

Maximum:

```text
500 characters
```

### Acceptance Criteria

Input updates preview immediately.

---

## FR-005 Bubble Template Selection

### Description

Users can select a predefined bubble design.

### Initial Templates

- Classic Bubble
- Modern Bubble
- Sticky Note

### Acceptance Criteria

Bubble changes instantly in preview.

---

## FR-006 Dynamic Bubble Sizing

### Description

Bubble dimensions must automatically adapt to text size.

### Width Formula

```text
Bubble Width =
Text Width + Horizontal Padding
```

### Height Formula

```text
Bubble Height =
Text Height + Vertical Padding
```

### Acceptance Criteria

Text never overflows bubble boundaries.

---

## FR-007 Text Wrapping

### Description

Long notes should wrap automatically.

### Rules

```text
Line exceeds maximum width
↓
Wrap to next line
```

### Acceptance Criteria

All text remains visible.

---

## FR-008 Bubble Positioning

### Version 1

Fixed positions:

- Top Left
- Top Right
- Bottom Left
- Bottom Right
- Center

### Future

Free drag-and-drop positioning.

---

## FR-009 Wallpaper Rendering

### Description

The system must generate a final composited bitmap.

### Layer Order

```text
Layer 1: Background Image
Layer 2: Bubble
Layer 3: Text
```

### Output

```text
Bitmap
```

---

## FR-010 Preview System

### Description

Users must see the generated wallpaper before applying it.

### Requirements

- Real-time updates
- Accurate representation
- No quality reduction

---

## FR-011 Wallpaper Application

### Description

Apply generated wallpaper directly.

### Supported Targets

- Home Screen
- Lock Screen
- Both

### Android API

```text
WallpaperManager
```

---

## FR-012 Export Wallpaper

### Description

Save wallpaper to storage.

### Output Format

```text
PNG
```

### Storage Location

```text
Pictures/NoteWallpaperMaker/
```

---

# 6. Non-Functional Requirements

---

## Performance

### NFR-001

Preview generation:

```text
< 100 ms
```

### NFR-002

Final wallpaper generation:

```text
< 1 second
```

### NFR-003

Memory consumption:

```text
< 300 MB
```

---

## Reliability

### NFR-004

No crashes for:

- Full HD images
- QHD images
- 4K images

---

## Security

### NFR-005

No internet access required.

### NFR-006

No external data transmission.

---

## Privacy

### NFR-007

User images never leave the device.

---

## Compatibility

### NFR-008

Minimum SDK:

```text
API 31
Android 12
```

### NFR-009

Target SDK:

```text
Latest Stable Android SDK
```

---

# 7. User Experience Flow

## Create Wallpaper Flow

```text
Launch Application
↓
Select Image
↓
Image Preview
↓
Enter Note
↓
Select Bubble Style
↓
Choose Bubble Position
↓
Generate Preview
↓
Apply Wallpaper
↓
Success
```

---

## Export Flow

```text
Launch Application
↓
Select Image
↓
Enter Note
↓
Generate Wallpaper
↓
Export PNG
↓
Success
```

---

# 8. System Architecture

## High-Level Architecture

```text
┌─────────────────────────┐
│      Android UI         │
└──────────┬──────────────┘
           │
           ▼
┌─────────────────────────┐
│    Editor Controller    │
└──────────┬──────────────┘
           │
 ┌─────────┼─────────┐
 ▼         ▼         ▼

Image   Bubble    Note
Module  Module    Module

 └─────────┬─────────┘
           ▼

 Wallpaper Renderer

           ▼

 Final Bitmap

           ▼

 Wallpaper Service
```

---

# 9. Rendering Architecture

## Rendering Pipeline

```text
User Image
↓
Image Decoder
↓
Bitmap
↓
Wallpaper Fit Engine
↓
Canvas Creation
↓
Bubble Layout Engine
↓
Text Measurement
↓
Bubble Rendering
↓
Text Rendering
↓
Bitmap Composition
↓
Final Wallpaper Bitmap
```

---

# 10. Technical Stack

## Language

```text
Kotlin
```

---

## UI Framework

```text
Jetpack Compose
Material 3
```

---

## Image Loading

```text
Coil
```

---

## Rendering

```text
Android Bitmap API
Android Canvas API
```

---

## Wallpaper Application

```text
WallpaperManager
```

---

## Build System

```text
Gradle Kotlin DSL
```

---

## Architecture Pattern

```text
MVVM
```

---

# 11. Project Structure

```text
app/

├── ui/
│   ├── screens/
│   │   ├── HomeScreen.kt
│   │   ├── EditorScreen.kt
│   │   ├── PreviewScreen.kt
│   │   └── ExportScreen.kt
│   │
│   └── components/
│       ├── BubbleSelector.kt
│       ├── NoteInput.kt
│       ├── PositionSelector.kt
│       └── WallpaperPreview.kt
│
├── viewmodel/
│   ├── EditorViewModel.kt
│   └── WallpaperViewModel.kt
│
├── renderer/
│   ├── WallpaperRenderer.kt
│   ├── BubbleRenderer.kt
│   ├── TextRenderer.kt
│   └── LayoutCalculator.kt
│
├── domain/
│   ├── model/
│   ├── repository/
│   └── usecase/
│
├── data/
│   └── BubbleRepository.kt
│
├── util/
│   ├── BitmapUtils.kt
│   ├── ScreenUtils.kt
│   └── ImageUtils.kt
│
├── assets/
│   └── bubbles/
│
└── MainActivity.kt
```

---

# 12. Data Models

## Note

```kotlin
data class Note(
    val text: String
)
```

---

## BubbleTemplate

```kotlin
data class BubbleTemplate(
    val id: String,
    val name: String,
    val assetPath: String
)
```

---

## WallpaperProject

```kotlin
data class WallpaperProject(
    val imageUri: Uri,
    val note: String,
    val bubbleTemplateId: String,
    val position: BubblePosition
)
```

---

# 13. Milestones

## Milestone 1

Foundation

- Android project setup
- Compose configuration
- Git repository

---

## Milestone 2

Image Module

- Image picker
- Image loading
- Image preview

---

## Milestone 3

Note Module

- Text input
- Bubble templates
- Dynamic sizing

---

## Milestone 4

Rendering Engine

- Canvas rendering
- Bitmap composition
- Preview generation

---

## Milestone 5

Wallpaper System

- WallpaperManager integration
- One-click wallpaper application

---

## Milestone 6

Export System

- PNG export
- Storage integration

---

# 14. Future Roadmap

## Version 1.1

- Multiple notes
- Multiple bubbles

## Version 1.2

- Bubble drag-and-drop
- Bubble resize handles

## Version 1.3

- Font customization
- Text color customization

## Version 2.0

- Sticker system
- Layer manager
- Template packs
- Advanced editor

---

# Definition of Done

The product is considered complete when:

- Users can select an image.
- Users can enter note text.
- Bubble size adjusts automatically.
- Wallpaper preview updates correctly.
- Final wallpaper renders successfully.
- Wallpaper can be applied directly.
- Entire workflow functions offline.
- No image data leaves the device.