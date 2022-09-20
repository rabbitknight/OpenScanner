
# For more information about using CMake with Android Studio, read the
# documentation: https://d.android.com/studio/projects/add-native-code.html

# Sets the minimum version of CMake required to build the native library.

cmake_minimum_required(VERSION 3.10.2)

# Declares and names the project.

project("wechat_qrcode_ncnn")
include_directories(include)
include_directories(include/opencv2/wechat_qrcode)
include_directories(wechat_qrcode_ncnn)
include_directories(include/opencv2/wechat_qrcode.hpp)

# Creates and names a library, sets it as either STATIC
# or SHARED, and provides the relative paths to its source code.
# You can define multiple libraries, and CMake builds them for you.
# Gradle automatically packages shared libraries with your APK.

file(GLOB_RECURSE SRC_QRCODE_NCNN "wechat_qrcode_ncnn/*.cpp")
file(GLOB_RECURSE SRC_ENGINE_NCNN "wechat_engine_ncnn/*.cpp")

add_library( # Sets the name of the library.
        wechat_qrcode_ncnn

        # Sets the library as a shared library.
        SHARED

        # Provides a relative path to your source file(s).
        ${SRC_QRCODE_NCNN})

add_library( # Sets the name of the library.
        wechat_engine_ncnn

        # Sets the library as a shared library.
        SHARED

        # Provides a relative path to your source file(s).
        ${SRC_ENGINE_NCNN})
add_library(
        opencv_java4
        SHARED
        IMPORTED
)
set_target_properties(opencv_java4 PROPERTIES IMPORTED_LOCATION
        ${CMAKE_SOURCE_DIR}/../../../../3rdpart/opencv/native/libs/${ANDROID_ABI}/libopencv_java4.so)

add_library(
        ncnn
        SHARED
        IMPORTED
)
set_target_properties(ncnn PROPERTIES IMPORTED_LOCATION
        ${CMAKE_SOURCE_DIR}/../jniLibs/${ANDROID_ABI}/lib/libncnn.so)


# Searches for a specified prebuilt library and stores the path as a
# variable. Because CMake includes system libraries in the search path by
# default, you only need to specify the name of the public NDK library
# you want to add. CMake verifies that the library exists before
# completing its build.

find_library( # Sets the name of the path variable.
        log-lib

        # Specifies the name of the NDK library that
        # you want CMake to locate.
        log)


# Specifies libraries CMake should link to your target library. You
# can link multiple libraries, such as libraries you define in this
# build script, prebuilt third-party libraries, or system libraries.

target_link_libraries( # Specifies the target library.
        wechat_qrcode_ncnn
        opencv_java4
        # Links the target library to the log library
        # included in the NDK.
        ${log-lib}
        ncnn
        )

target_link_libraries( # Specifies the target library.
        wechat_engine_ncnn
        wechat_qrcode_ncnn
        opencv_java4
        # Links the target library to the log library
        # included in the NDK.
        ${log-lib}
        ncnn
        )