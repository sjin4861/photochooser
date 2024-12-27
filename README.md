# PhotoChooser
<img width="296" alt="스크린샷 2024-12-27 오후 3 37 29" src="https://github.com/user-attachments/assets/0805029c-4571-4080-8cd1-e364aac37757" />
<img width="296" alt="스크린샷 2024-12-27 오후 3 37 12" src="https://github.com/user-attachments/assets/953f0e82-abf4-4255-8c19-eea30ec4a67a" />

PhotoChooser는 두 장의 사진을 선택하고, 선택한 사진을 GPT API에 전송하여 추천 결과를 받는 Flutter 애플리케이션입니다. 이 앱은 3학년 1학기 ‘소프트웨어설계및실험’ 과목의 텀프로젝트로 Kotlin으로 개발했던 앱을 Flutter로 재구현한 것입니다.

## 기능 소개

-	이미지 선택: 갤러리에서 두 장의 이미지를 선택합니다.
-	이미지 인코딩: 선택한 이미지를 Base64로 인코딩하여 API에 전송합니다.
-	API 요청: OpenAI의 GPT API를 사용하여 이미지에 대한 추천 결과를 받습니다.
-	결과 표시: API로부터 받은 추천 결과를 화면에 표시하고, 선택된 이미지를 보여줍니다.
-	설정 변경: API 키, Temperature, Max Tokens 등의 설정을 변경할 수 있습니다.

## 프로젝트 구조
~~~bash
lib/
├── main.dart
├── screens/
│   ├── main_screen.dart
│   ├── result_screen.dart
│   └── settings_screen.dart
├── services/
│   └── api_service.dart
├── utils/
│   └── constants.dart
~~~

-	main.dart: 앱의 진입점입니다.
-	screens/: 각 화면에 대한 위젯이 포함되어 있습니다.
-	main_screen.dart: 메인 화면으로, 이미지 선택 및 API 요청을 처리합니다.
-	result_screen.dart: 결과 화면으로, API 응답을 표시합니다.
-	settings_screen.dart: 설정 화면으로, API 키 및 기타 설정을 변경할 수 있습니다.
-	services/: API 통신 관련 코드가 포함되어 있습니다.
-	api_service.dart: API 요청 및 응답 처리를 담당합니다.
-	utils/: 앱에서 사용하는 상수값 등이 포함되어 있습니다.
-	constants.dart: API 키, 기본 메시지, 설정 값 등이 정의되어 있습니다.


## 설치 및 실행 방법

**필요한 도구**

-	Flutter SDK: Flutter 2.0 이상
-	Android Studio 또는 Visual Studio Code
-	Android 또는 iOS 에뮬레이터, 또는 실제 디바이스

**프로젝트 클론 및 설정**


~~~bash
# 프로젝트를 클론합니다.
git clone https://github.com/yourusername/photo_chooser.git

# 프로젝트 디렉토리로 이동합니다.
cd photo_chooser

# Flutter 패키지를 설치합니다.
flutter pub get

flutter run
~~~

## 사용된 패키지

-	image_picker: 갤러리에서 이미지 선택
-	http: API 통신
-	shared_preferences: 설정 값 저장
