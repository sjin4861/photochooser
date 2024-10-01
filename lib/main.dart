import 'package:flutter/material.dart';
import 'screens/main_screen.dart';

void main() {
  runApp(PhotoChooserApp());
}

class PhotoChooserApp extends StatelessWidget {
  @override
  Widget build(BuildContext context) {
    return MaterialApp(
      title: 'Photo Chooser',
      theme: ThemeData(
        primarySwatch: Colors.blue,
      ),
      home: MainScreen(),
    );
  }
}