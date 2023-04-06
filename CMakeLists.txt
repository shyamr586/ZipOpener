cmake_minimum_required(VERSION 3.16)

project(ZipFileOpener VERSION 0.1 LANGUAGES CXX)

set(CMAKE_CXX_STANDARD_REQUIRED ON)

find_package(Qt6 6.4 REQUIRED COMPONENTS Quick)

qt_standard_project_setup()

qt_add_executable(appZipFileOpener
    main.cpp
    filepicker.h
    filepicker.cpp
)

qt_add_resources(appZipFileOpener "android"
    PREFIX /
    FILES
        android/src/com/example/zipopener/MainActivity.java
        android/AndroidManifest.xml
        android/build.gradle
        android/res/values/libs.xml
)

qt_add_qml_module(appZipFileOpener
    URI ZipFileOpener
    VERSION 1.0
    QML_FILES Main.qml
)

set_target_properties(appZipFileOpener PROPERTIES
    MACOSX_BUNDLE_GUI_IDENTIFIER my.example.com
    MACOSX_BUNDLE_BUNDLE_VERSION ${PROJECT_VERSION}
    MACOSX_BUNDLE_SHORT_VERSION_STRING ${PROJECT_VERSION_MAJOR}.${PROJECT_VERSION_MINOR}
    MACOSX_BUNDLE TRUE
    WIN32_EXECUTABLE TRUE
    QT_ANDROID_PACKAGE_SOURCE_DIR ${CMAKE_CURRENT_SOURCE_DIR}/android
)

target_link_libraries(appZipFileOpener
    PRIVATE Qt6::Quick
)

install(TARGETS appZipFileOpener
    BUNDLE DESTINATION .
    LIBRARY DESTINATION ${CMAKE_INSTALL_LIBDIR})

