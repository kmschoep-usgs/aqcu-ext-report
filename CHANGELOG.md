# Changelog
All notable changes to this project will be documented in this file.

The format is based on [Keep a Changelog](http://keepachangelog.com/en/1.0.0/)
and this project adheres to [Semantic Versioning](http://semver.org/spec/v2.0.0.html). (Patch version X.Y.0 is implied if not specified.)

## [Unreleased]
### Added

### Changed
- AQ Qualifiers remapped to Extremes report-specific Qualifier model
- Qualifiers for daily value timeseries have time removed from date
- Add performance logging for report builder
- Add debug log statements
- Add additional try/catch error logging
- Enable logging of application
- update aqcu-framework version to 0.0.6-SNAPSHOT

## [0.0.1] - 2019-03-01
### Added
- Initial release - happy path
- Specific timeout values
- Default Aquarius timeout value of 30000

### Changed
- Simplified extremes logic
- Use streams() instead of loops, where possible
- Aquarius SDK verson 18.8.1
- AQCU Framework version 0.0.5

### Removed
- Disabled TLS 1.0/1.1 by default.

[Unreleased]: https://github.com/USGS-CIDA/aqcu-ext-report/compare/aqcu-ext-report-0.0.1...master