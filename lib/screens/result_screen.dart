import 'dart:io';
import 'package:flutter/material.dart';

class ResultScreen extends StatelessWidget {
  final String resultText;
  final File image1;
  final File image2;

  ResultScreen({
    required this.resultText,
    required this.image1,
    required this.image2,
  });

  @override
  Widget build(BuildContext context) {
    // 원본 로직: API 답변에서 "첫 번째"라는 문구 포함되면 image1, 아니면 image2
    File selectedImage = resultText.contains('첫 번째') ? image1 : image2;

    return Scaffold(
      backgroundColor: Colors.white,
      // 기존 AppBar 대신 SafeArea + Column 사용
      body: SafeArea(
        child: Column(
          children: [
            /// 1) 상단 바 (뒤로가기 버튼 + "게시물" 텍스트)
            Container(
              height: 60,
              padding: const EdgeInsets.symmetric(horizontal: 12),
              child: Row(
                children: [
                  // 뒤로가기 버튼 (원본 XML에서는 @drawable/back 이미지를 사용)
                  IconButton(
                    iconSize: 30,
                    icon: Image.asset('assets/back.png'), // <-- 에셋 이미지 사용 시
                    onPressed: () {
                      Navigator.pop(context); // 단순 뒤로가기
                    },
                  ),
                  // " 게시물" 텍스트
                  Text(
                    ' 게시물',
                    style: TextStyle(
                      fontSize: 25,
                      fontWeight: FontWeight.bold,
                      color: Colors.black,
                    ),
                  ),
                ],
              ),
            ),

            /// 2) 메인 콘텐츠 영역 (9.2 비율로 가정 → Expanded)
            Expanded(
              child: Column(
                children: [
                  /// 2-1) 사용자 정보 행 (이미지 + "photo_chooser" 텍스트)
                  Container(
                    height: 50, // 원본은 layout_weight=0.8 정도로 설정됨
                    padding: const EdgeInsets.only(left: 10),
                    child: Row(
                      children: [
                        // pnu 로고 (원본 XML에서 @drawable/pnu)
                        Container(
                          width: 50,
                          height: 40,
                          child: Image.asset(
                            'assets/pnu.png',
                            fit: BoxFit.cover,
                          ),
                        ),
                        const SizedBox(width: 10),
                        Expanded(
                          child: Text(
                            'photo_chooser',
                            style: TextStyle(
                              color: Colors.black,
                              fontWeight: FontWeight.bold,
                              fontSize: 20,
                            ),
                          ),
                        ),
                      ],
                    ),
                  ),

                  /// 2-2) 결과로 선택된 이미지 (layout_weight=4.4)
                  Expanded(
                    flex: 4,
                    child: Container(
                      color: Colors.grey[200], // 없애거나 원하는 배경색 사용
                      child: Image.file(
                        selectedImage,
                        fit: BoxFit.cover,
                      ),
                    ),
                  ),

                  /// 2-3) 결과 텍스트 (layout_weight=4.0) → ScrollView
                  Expanded(
                    flex: 4,
                    child: SingleChildScrollView(
                      child: Container(
                        padding: const EdgeInsets.all(16.0),
                        child: Text(
                          resultText,
                          style: TextStyle(fontSize: 18, color: Colors.black),
                        ),
                      ),
                    ),
                  ),

                  /// 2-4) 하단 아이콘 메뉴 (layout_weight=0.7)
                  Container(
                    height: 50,
                    child: Row(
                      mainAxisAlignment: MainAxisAlignment.spaceAround,
                      children: [
                        // Home 아이콘
                        InkWell(
                          onTap: () {
                            // 원본에서는 "처음으로" 버튼 클릭 시 메인 화면으로 돌아가는 로직
                            // Navigator.popUntil(context, ModalRoute.withName('/'));
                            // 등으로 커스텀 가능
                            Navigator.popUntil(context, (route) => route.isFirst);
                          },
                          child: Image.asset(
                            'assets/home.png',
                            width: 30,
                            height: 30,
                          ),
                        ),
                        // Magnifier 아이콘
                        Image.asset(
                          'assets/magnifier.png',
                          width: 30,
                          height: 30,
                        ),
                        // Plus 아이콘
                        Image.asset(
                          'assets/plus.png',
                          width: 30,
                          height: 30,
                        ),
                        // Reels 아이콘
                        Image.asset(
                          'assets/reels.png',
                          width: 30,
                          height: 30,
                        ),
                        // Profile 아이콘
                        Image.asset(
                          'assets/profile.png',
                          width: 30,
                          height: 30,
                        ),
                      ],
                    ),
                  ),
                ],
              ),
            ),
          ],
        ),
      ),
    );
  }
}