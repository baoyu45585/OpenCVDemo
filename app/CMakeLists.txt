# For more information about using CMake with Android Studio, read the
# documentation: https://d.android.com/studio/projects/add-native-code.html

# Sets the minimum version of CMake required to build the native library.

cmake_minimum_required(VERSION 3.4.1)

include_directories(src/main/cpp/include)

add_library( # Sets the name of the library.
             native-lib

             SHARED

              src/main/cpp/native-lib.cpp
              src/main/cpp/WlFace.cpp
              src/main/cpp/CascadeDetectorAdapter.cpp
             )

find_library(
              log-lib
              log )

add_library( libOpenCV_10 SHARED IMPORTED)
set_target_properties( libOpenCV_10
                       PROPERTIES IMPORTED_LOCATION
                       ${CMAKE_SOURCE_DIR}/src/main/jniLibs/${ANDROID_ABI}/libopencv_java3.so)




target_link_libraries( # Specifies the target library.
                       native-lib
                       libOpenCV_10

                       ${log-lib} )