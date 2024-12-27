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
    File selectedImage = resultText.contains('첫 번째') ? image1 : image2;

    return Scaffold(
      appBar: AppBar(
        title: Text('결과'),
      ),
      body: SingleChildScrollView( // 스크롤 가능하도록 수정
        child: Padding(
          padding: EdgeInsets.all(16.0),
          child: Column(
            crossAxisAlignment: CrossAxisAlignment.start,
            children: [
              Text(
                resultText,
                style: TextStyle(fontSize: 16.0),
              ),
              SizedBox(height: 16),
              Image.file(selectedImage),
              SizedBox(height: 16),
              Center(
                child: ElevatedButton(
                  onPressed: () {
                    Navigator.popUntil(context, ModalRoute.withName('/'));
                  },
                  child: Text('처음으로'),
                ),
              ),
            ],
          ),
        ),
      ),
    );
  }
}