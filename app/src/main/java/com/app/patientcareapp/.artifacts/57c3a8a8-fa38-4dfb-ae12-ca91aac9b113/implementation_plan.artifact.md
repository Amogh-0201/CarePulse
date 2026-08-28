# Dark Mode Visibility Improvements

Enhance the visibility of UI components (Cards, Search Bars, Text Fields) in Dark Mode by adding subtle borders and refining contrast, ensuring zero impact on the Light Theme's appearance.

## User Review Required

> [!IMPORTANT]
> The improvements rely on adding a subtle border to elevated surfaces in Dark Mode. This is a common Material 3 pattern for dark themes where elevation shadows are less effective.

## Proposed Changes

### Core UI Logic

#### [MODIFY] [Theme.kt](file:///D:/Projects/PatientCareApp/app/src/main/java/com/app/patientcareapp/ui/theme/Theme.kt)
- Define a standard border color that adapts to the current theme (visible in Dark Mode, effectively invisible in Light Mode).
- Add `outline` color to `LightColorScheme` to ensure consistency.

---

### Home Feature

#### [MODIFY] [HomeScreen.kt](file:///D:/Projects/PatientCareApp/app/src/main/java/com/app/patientcareapp/feature_home/presentation/HomeScreen.kt)
- Add subtle borders to `UpcomingMedicineMiniCard`, `HealthSummaryMiniCard`, `MedicineItemCard`, and `BatteryWarningCard` in Dark Mode.

---

### Health Records Feature

#### [MODIFY] [HealthRecordsScreen.kt](file:///D:/Projects/PatientCareApp/app/src/main/java/com/app/patientcareapp/feature_health_records/presentation/health_records/HealthRecordsScreen.kt)
- Add border to the Search Bar in Dark Mode (currently transparent).
- Add border to `HealthRecordCard`.

#### [MODIFY] [HealthRecordViewerScreen.kt](file:///D:/Projects/PatientCareApp/app/src/main/java/com/app/patientcareapp/feature_health_records/presentation/health_record_viewer/HealthRecordViewerScreen.kt)
- Add borders to all detail cards.

---

### Profile Feature

#### [MODIFY] [ProfileScreen.kt](file:///D:/Projects/PatientCareApp/app/src/main/java/com/app/patientcareapp/feature_profile/presentation/profile/ProfileScreen.kt)
- Add border to `PremiumProfileCard`.

#### [MODIFY] [EditProfileScreen.kt](file:///D:/Projects/PatientCareApp/app/src/main/java/com/app/patientcareapp/feature_profile/presentation/edit_profile/EditProfileScreen.kt)
- Add borders to section cards and ensure text fields have visible outlines in Dark Mode.

---

### Medication Reminders Feature

#### [MODIFY] [AddEditMedReminderScreen.kt](file:///D:/Projects/PatientCareApp/app/src/main/java/com/app/patientcareapp/feature_med_reminder/presentation/add_edit_med_reminders/AddEditMedReminderScreen.kt)
- Add borders to `PremiumFormCard` and refine text field outlines.

---

### Core Components

#### [MODIFY] [AppSnackbar.kt](file:///D:/Projects/PatientCareApp/app/src/main/java/com/app/patientcareapp/core/presentation/components/AppSnackbar.kt)
- Add border to the Snackbar card for better visibility against dark backgrounds.

## Verification Plan

### Automated Tests
- Run `gradle build` to ensure no regressions in layout logic.

### Manual Verification
- Visual inspection of all modified screens in both Light and Dark themes.
- Verify that card edges are clearly defined in Dark Mode.
- Verify that Search Bars are easily identifiable in Dark Mode.
- Confirm that the Light Theme remains identical to its current "great" state.
