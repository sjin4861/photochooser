import 'dart:io';
import 'dart:convert';
import 'package:flutter/material.dart';
import 'package:image_picker/image_picker.dart';
import 'package:photochooser_flutter/services/api_service.dart';
import 'package:photochooser_flutter/screens/result_screen.dart';
import 'settings_screen.dart';
import 'package:shared_preferences/shared_preferences.dart';
import 'package:photochooser_flutter/utils/constants.dart';

class MainScreen extends StatefulWidget {
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

  _loadConstants() async {
    SharedPreferences prefs = await SharedPreferences.getInstance();
    setState(() {
      Constants.apiKey = prefs.getString('apiKey') ?? Constants.apiKey;
      Constants.temperature = prefs.getDouble('temperature') ?? Constants.temperature;
      Constants.maxTokens = prefs.getInt('maxTokens') ?? Constants.maxTokens;
    });
  }

  Future _getImage(int imageNumber) async {
    final pickedFile = await picker.pickImage(source: ImageSource.gallery);

    setState(() {
      if (pickedFile != null) {
        if (imageNumber == 1) {
          _image1 = File(pickedFile.path);
        } else {
          _image2 = File(pickedFile.path);
        }
      }
    });
  }

  _sendRequest() async {
    if (_image1 == null || _image2 == null) {
      setState(() {
        _statusText = '사진을 두 개 선택해주세요.';
      });
      return;
    }

    setState(() {
      _statusText = '요청 중입니다. 잠시만 기다려주세요.';
    });

    String base64Image1 = base64Encode(await _image1!.readAsBytes());
    String base64Image2 = base64Encode(await _image2!.readAsBytes());

    // 필요한 경우 이미지 데이터 앞에 "data:image/jpeg;base64," 추가
   
    base64Image1 = 'data:image/jpeg;base64,$base64Image1';
    base64Image2 = 'data:image/jpeg;base64,$base64Image2';
    // 압축 실시
    base64Image1 = base64Image1.substring(0, 100);
    base64Image2 = base64Image2.substring(0, 100);

    // 100자까지만 출력 
    print(base64Image1.substring(0, 100));
  

    try {
      String? result = await ApiService.getRecommendation(base64Image1, base64Image2);
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

  _openSettings() {
    Navigator.push(
      context,
      MaterialPageRoute(builder: (context) => SettingsScreen()),
    ).then((_) {
      _loadConstants(); // 설정값 다시 로드
    });
  }

  @override
  Widget build(BuildContext context) {
    return Scaffold(
      appBar: AppBar(
        title: Text('사진 선택기'),
        actions: [
          IconButton(
            icon: Icon(Icons.settings),
            onPressed: _openSettings,
          ),
        ],
      ),
      body: Padding(
        padding: EdgeInsets.all(16.0),
        child: Column(
          children: [
            Text(_statusText),
            SizedBox(height: 16),
            Row(
              mainAxisAlignment: MainAxisAlignment.spaceAround,
              children: [
                _imagePreview(_image1, 1),
                _imagePreview(_image2, 2),
              ],
            ),
            SizedBox(height: 16),
            ElevatedButton(
              onPressed: _sendRequest,
              child: Text('추천 받기'),
            ),
          ],
        ),
      ),
    );
  }

  Widget _imagePreview(File? image, int imageNumber) {
    return GestureDetector(
      onTap: () => _getImage(imageNumber),
      child: Container(
        width: 150,
        height: 150,
        color: Colors.grey[300],
        child: image == null
            ? Icon(Icons.add_a_photo, size: 50)
            : Image.file(image, fit: BoxFit.cover),
      ),
    );
  }
}