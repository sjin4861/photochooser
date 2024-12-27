import 'dart:io';
import 'dart:convert';
import 'package:flutter/material.dart';
import 'package:image_picker/image_picker.dart';
import 'package:shared_preferences/shared_preferences.dart';
import 'package:photochooser_flutter/services/api_service.dart';
import 'package:photochooser_flutter/screens/result_screen.dart';
import 'package:photochooser_flutter/screens/settings_screen.dart';
import 'package:photochooser_flutter/utils/constants.dart';
import 'package:flutter_image_compress/flutter_image_compress.dart';

class MainScreen extends StatefulWidget {
  const MainScreen({Key? key}) : super(key: key);

  @override
  _MainScreenState createState() => _MainScreenState();
}

class _MainScreenState extends State<MainScreen> {
  File? _image1;
  File? _image2;
  final picker = ImagePicker();
  String _statusText = '';

  @override
  void initState() {
    super.initState();
    _loadConstants();
  }

  Future<void> _loadConstants() async {
    SharedPreferences prefs = await SharedPreferences.getInstance();
    setState(() {
      Constants.apiKey = prefs.getString('apiKey') ?? Constants.apiKey;
      Constants.temperature = prefs.getDouble('temperature') ?? Constants.temperature;
      Constants.maxTokens = prefs.getInt('maxTokens') ?? Constants.maxTokens;
    });
  }

  Future<void> _getImage(int imageNumber) async {
    final pickedFile = await picker.pickImage(source: ImageSource.gallery);

    if (pickedFile != null) {
      setState(() {
        if (imageNumber == 1) {
          _image1 = File(pickedFile.path);
        } else {
          _image2 = File(pickedFile.path);
        }
      });
    }
  }

  Future<void> _sendRequest() async {
    if (_image1 == null || _image2 == null) {
      setState(() {
        _statusText = '사진을 두 개 선택해주세요.';
      });
      return;
    }

    setState(() {
      _statusText = '요청 중입니다. 잠시만 기다려주세요.';
    });

    try {
      // 실제로는 전체 base64를 전송해야 하지만, 
      // 디버깅 편의를 위해 콘솔 출력만 100자로 제한한다고 가정
      String fullBase64Image1 = await compressAndEncodeBase64(_image1!);
      String fullBase64Image2 = await compressAndEncodeBase64(_image2!);

      print('[Debug] img1: ${fullBase64Image1.substring(0, 100)}...');
      print('[Debug] img2: ${fullBase64Image2.substring(0, 100)}...');

      // 여기서부터는 전체 문자열을 사용
      String? result = await ApiService.getRecommendation(fullBase64Image1, fullBase64Image2);
      if (result != null) {
        Navigator.push(
          context,
          MaterialPageRoute(
            builder: (context) => ResultScreen(
              resultText: result,
              image1: _image1!,
              image2: _image2!,
            ),
          ),
        );
      } else {
        setState(() {
          _statusText = '결과를 가져오지 못했습니다.';
        });
      }
    } catch (e) {
      setState(() {
        _statusText = '오류 발생: $e';
      });
    }
  }

  void _openSettings() {
    Navigator.push(
      context,
      MaterialPageRoute(builder: (context) => SettingsScreen()),
    ).then((_) {
      _loadConstants();
    });
  }

  @override
  Widget build(BuildContext context) {
    // 상단 아이콘 (혹은 설정 버튼)과 타이틀을 커스텀 디자인으로 만들기 위해
    // AppBar 대신 body에 직접 Row를 배치하는 예시
    return Scaffold(
      backgroundColor: Colors.white,
      body: SafeArea(
        child: Column(
          children: [
            // 상단 레이아웃 (PhotoChooser 텍스트 + 설정 버튼)
            Container(
              padding: const EdgeInsets.symmetric(horizontal: 16),
              height: 60,
              child: Row(
                children: [
                  Expanded(
                    flex: 8,
                    child: Text(
                      'PhotoChooser',
                      style: TextStyle(
                        fontSize: 28,
                        fontWeight: FontWeight.bold,
                        color: Colors.black87,
                      ),
                    ),
                  ),
                  IconButton(
                    icon: Icon(Icons.settings, color: Colors.black87),
                    onPressed: _openSettings,
                  ),
                ],
              ),
            ),
            // 첫 번째 이미지
            Expanded(
              flex: 4,
              child: Container(
                margin: const EdgeInsets.symmetric(horizontal: 16, vertical: 8),
                decoration: BoxDecoration(
                  color: Colors.grey[200],
                  borderRadius: BorderRadius.circular(8),
                ),
                child: _imagePreview(_image1, 1),
              ),
            ),
            // 두 번째 이미지
            Expanded(
              flex: 4,
              child: Container(
                margin: const EdgeInsets.symmetric(horizontal: 16, vertical: 8),
                decoration: BoxDecoration(
                  color: Colors.grey[200],
                  borderRadius: BorderRadius.circular(8),
                ),
                child: _imagePreview(_image2, 2),
              ),
            ),
            // 상태 메시지 (혹은 안내 문구)
            Padding(
              padding: const EdgeInsets.all(8.0),
              child: Text(
                _statusText.isEmpty
                    ? '고민 중인 사진을 2장 올리고 버튼을 누르세요!'
                    : _statusText,
                textAlign: TextAlign.center,
                style: TextStyle(fontSize: 16, color: Colors.black87),
              ),
            ),
            // 추천 받기 버튼
            Container(
              width: double.infinity,
              margin: const EdgeInsets.symmetric(horizontal: 16, vertical: 8),
              child: ElevatedButton(
                style: ElevatedButton.styleFrom(
                  backgroundColor: Color(0xFF3394EA), // 파란색 계열
                  minimumSize: Size.fromHeight(50),
                ),
                onPressed: _sendRequest,
                child: Text(
                  '추천 받기',
                  style: TextStyle(fontSize: 18, color: Colors.white),
                ),
              ),
            ),
          ],
        ),
      ),
    );
  }

  Widget _imagePreview(File? image, int imageNumber) {
    if (image == null) {
      return InkWell(
        onTap: () => _getImage(imageNumber),
        child: Center(
          child: Icon(Icons.add_a_photo, size: 48, color: Colors.grey[600]),
        ),
      );
    } else {
      return InkWell(
        onTap: () => _getImage(imageNumber),
        child: ClipRRect(
          borderRadius: BorderRadius.circular(8),
          child: Image.file(image, fit: BoxFit.cover),
        ),
      );
    }
  }
  
  Future<String> compressAndEncodeBase64(File file) async {
    final result = await FlutterImageCompress.compressWithFile(
      file.absolute.path,
      minWidth: 400,   // 원하는 최소 가로 크기
      minHeight: 300,  // 원하는 최소 세로 크기
      quality: 30,     // 압축율(0~100)
      format: CompressFormat.jpeg,
    );

    if (result == null) {
      throw Exception("이미지 압축 실패");
    }

    return "data:image/jpeg;base64," + base64Encode(result);
  }
}

