#include <jni.h>
#include <windows.h>
#include <mmdeviceapi.h>
#include <endpointvolume.h>

#include <algorithm>
#include <cmath>
#include <cstdio>

namespace {

template <typename Interface>
class ComPointer {
 public:
  ComPointer() : pointer_(nullptr) {
  }

  ~ComPointer() {
    if (pointer_ != nullptr) {
      pointer_->Release();
    }
  }

  ComPointer(const ComPointer&) = delete;
  ComPointer& operator=(const ComPointer&) = delete;

  Interface** address() {
    return &pointer_;
  }

  Interface* operator->() const {
    return pointer_;
  }

 private:
  Interface* pointer_;
};

class ComScope {
 public:
  ComScope() {
    initializeResult_ = CoInitializeEx(nullptr, COINIT_MULTITHREADED);
    shouldUninitialize_ = SUCCEEDED(initializeResult_);
  }

  ~ComScope() {
    if (shouldUninitialize_) {
      CoUninitialize();
    }
  }

  ComScope(const ComScope&) = delete;
  ComScope& operator=(const ComScope&) = delete;

  bool isUsable() const {
    return shouldUninitialize_ || initializeResult_ == RPC_E_CHANGED_MODE;
  }

  HRESULT initializeResult() const {
    return initializeResult_;
  }

 private:
  HRESULT initializeResult_;
  bool shouldUninitialize_;
};

void throwIoException(JNIEnv* environment, const char* operation, HRESULT result) {
  char message[160];

  std::snprintf(
      message,
      sizeof(message),
      "%s failed (HRESULT 0x%08lX)",
      operation,
      static_cast<unsigned long>(result));

  jclass exceptionClass = environment->FindClass("java/io/IOException");

  if (exceptionClass != nullptr) {
    environment->ThrowNew(exceptionClass, message);
  }
}

HRESULT acquireEndpointVolume(ComPointer<IAudioEndpointVolume>& endpointVolume) {
  ComPointer<IMMDeviceEnumerator> enumerator;

  HRESULT result = CoCreateInstance(
      __uuidof(MMDeviceEnumerator),
      nullptr,
      CLSCTX_ALL,
      __uuidof(IMMDeviceEnumerator),
      reinterpret_cast<void**>(enumerator.address()));

  if (FAILED(result)) {
    return result;
  }

  ComPointer<IMMDevice> device;

  result = enumerator->GetDefaultAudioEndpoint(eRender, eMultimedia, device.address());

  if (FAILED(result)) {
    return result;
  }

  return device->Activate(
      __uuidof(IAudioEndpointVolume),
      CLSCTX_ALL,
      nullptr,
      reinterpret_cast<void**>(endpointVolume.address()));
}

bool openEndpointVolume(
    JNIEnv* environment,
    const ComScope& scope,
    ComPointer<IAudioEndpointVolume>& endpointVolume) {
  if (!scope.isUsable()) {
    throwIoException(environment, "CoInitializeEx", scope.initializeResult());

    return false;
  }

  HRESULT result = acquireEndpointVolume(endpointVolume);

  if (FAILED(result)) {
    throwIoException(environment, "Opening the default audio endpoint", result);

    return false;
  }

  return true;
}

}

extern "C" {

JNIEXPORT jint JNICALL Java_vertexlink_controller_audio_NativeWindowsAudio_getVolumePercent(
    JNIEnv* environment,
    jclass) {
  ComScope scope;
  ComPointer<IAudioEndpointVolume> endpointVolume;

  if (!openEndpointVolume(environment, scope, endpointVolume)) {
    return 0;
  }

  float level = 0.0f;
  HRESULT result = endpointVolume->GetMasterVolumeLevelScalar(&level);

  if (FAILED(result)) {
    throwIoException(environment, "GetMasterVolumeLevelScalar", result);

    return 0;
  }

  return static_cast<jint>(std::lround(level * 100.0f));
}

JNIEXPORT void JNICALL Java_vertexlink_controller_audio_NativeWindowsAudio_setVolumePercent(
    JNIEnv* environment,
    jclass,
    jint percent) {
  ComScope scope;
  ComPointer<IAudioEndpointVolume> endpointVolume;

  if (!openEndpointVolume(environment, scope, endpointVolume)) {
    return;
  }

  int clampedPercent = std::min(100, std::max(0, static_cast<int>(percent)));
  HRESULT result = endpointVolume->SetMasterVolumeLevelScalar(clampedPercent / 100.0f, nullptr);

  if (FAILED(result)) {
    throwIoException(environment, "SetMasterVolumeLevelScalar", result);
  }
}

JNIEXPORT jboolean JNICALL Java_vertexlink_controller_audio_NativeWindowsAudio_isMuted(
    JNIEnv* environment,
    jclass) {
  ComScope scope;
  ComPointer<IAudioEndpointVolume> endpointVolume;

  if (!openEndpointVolume(environment, scope, endpointVolume)) {
    return JNI_FALSE;
  }

  BOOL isMuted = FALSE;
  HRESULT result = endpointVolume->GetMute(&isMuted);

  if (FAILED(result)) {
    throwIoException(environment, "GetMute", result);

    return JNI_FALSE;
  }

  return isMuted ? JNI_TRUE : JNI_FALSE;
}

JNIEXPORT void JNICALL Java_vertexlink_controller_audio_NativeWindowsAudio_setMuted(
    JNIEnv* environment,
    jclass,
    jboolean isMuted) {
  ComScope scope;
  ComPointer<IAudioEndpointVolume> endpointVolume;

  if (!openEndpointVolume(environment, scope, endpointVolume)) {
    return;
  }

  HRESULT result = endpointVolume->SetMute(isMuted == JNI_TRUE ? TRUE : FALSE, nullptr);

  if (FAILED(result)) {
    throwIoException(environment, "SetMute", result);
  }
}

}
