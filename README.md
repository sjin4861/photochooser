사진을 2개 올리면 base64로 인코딩 후 GPT-4o 모델에게 추천을 요구합니다. 

응답은 사진 추천 이유와 게시용 추천 멘트를 받습니다.

<메인 화면>
<img width="202" alt="image" src="https://github.com/sjin4861/photochooser/assets/32785250/c945aa12-364b-4e03-bcaa-18765d68ce88">

<응답 화면>
<img width="201" alt="image" src="https://github.com/sjin4861/photochooser/assets/32785250/691b29bc-f1bf-47af-a11f-19d0766b7984">

------------------디렉토리 구조------------------
~~~
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
~~~
