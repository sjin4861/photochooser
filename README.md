사진을 2개 올리면 base64로 인코딩 후 GPT-4o 모델에게 추천을 요구합니다. 

응답은 사진 추천 이유와 게시용 추천 멘트를 받습니다.

------------------디렉토리 구조------------------
MyProject/
├── app/
│   ├── src/
│   │   ├── main/
│   │   │   ├── java/
│   │   │   │   ├── com/
│   │   │   │   │   ├── example/
│   │   │   │   │   │   ├── photochooser/
│   │   │   │   │   │   │   ├──adapter/
│   │   │   │   │   │   │   ├──data/
│   │   │   │   │   │   │   │   ├──api/
│   │   │   │   │   │   │   │   │   ├── GptAPI.kt
│   │   │   │   │   │   │   ├──ui/
│   │   │   │   │   │   │   │   │   ├── MainActivity.kt
│   │   │   │   │   │   │   │   │   ├── GalleryActivity.kt
│   │   │   │   │   │   │   │   │   ├── SettingActivity.kt
│   │   │   │   │   │   │   │   │   ├── ResultActivity.kt
│   │   │   │   │   │   │   ├──di/
│   │   │   │   │   │   │   ├──utils/
│   │   │   │   │   │   │   │   │   ├── Constant.kt
│   │   │   ├── res/
│   │   │   │   ├── drawable
│   │   │   │   │   ├── pictures.jpeg
│   │   │   │   ├── layout
│   │   │   │   │   ├── activity_main.xml
│   │   │   │   │   ├── activity_gallery.xml
│   │   │   │   │   ├── activity_result.xml
│   │   │   │   │   ├── activity_setting.xml
