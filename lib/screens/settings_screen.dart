import 'package:flutter/material.dart';
import 'package:shared_preferences/shared_preferences.dart';
import 'package:photochooser_flutter/utils/constants.dart';

class SettingsScreen extends StatefulWidget {
  @override
  _SettingsScreenState createState() => _SettingsScreenState();
}

class _SettingsScreenState extends State<SettingsScreen> {
  final TextEditingController _apiKeyController = TextEditingController();
  double _temperature = Constants.temperature;
  int _maxTokens = Constants.maxTokens;

  @override
  void initState() {
    super.initState();
    _loadSettings();
  }

  _loadSettings() async {
    SharedPreferences prefs = await SharedPreferences.getInstance();
    setState(() {
      _apiKeyController.text = prefs.getString('apiKey') ?? Constants.apiKey;
      _temperature = prefs.getDouble('temperature') ?? Constants.temperature;
      _maxTokens = prefs.getInt('maxTokens') ?? Constants.maxTokens;
    });
  }

  _saveSettings() async {
    SharedPreferences prefs = await SharedPreferences.getInstance();
    await prefs.setString('apiKey', _apiKeyController.text);
    await prefs.setDouble('temperature', _temperature);
    await prefs.setInt('maxTokens', _maxTokens);
    // Constants 값도 업데이트
    Constants.apiKey = _apiKeyController.text;
    Constants.temperature = _temperature;
    Constants.maxTokens = _maxTokens;
    Navigator.pop(context);
  }

  @override
  Widget build(BuildContext context) {
    return Scaffold(
      appBar: AppBar(
        title: Text('설정'),
      ),
      body: Padding(
        padding: EdgeInsets.all(16.0),
        child: Column(
          children: [
            TextField(
              controller: _apiKeyController,
              decoration: InputDecoration(labelText: 'API Key'),
            ),
            SizedBox(height: 16),
            Text('Temperature: ${_temperature.toStringAsFixed(1)}'),
            Slider(
              value: _temperature,
              min: 0,
              max: 1,
              divisions: 10,
              label: _temperature.toStringAsFixed(1),
              onChanged: (value) {
                setState(() {
                  _temperature = value;
                });
              },
            ),
            SizedBox(height: 16),
            Text('Max Tokens: $_maxTokens'),
            Slider(
              value: _maxTokens.toDouble(),
              min: 500,
              max: 3000,
              divisions: 10,
              label: _maxTokens.toString(),
              onChanged: (value) {
                setState(() {
                  _maxTokens = value.toInt();
                });
              },
            ),
            SizedBox(height: 16),
            ElevatedButton(
              onPressed: _saveSettings,
              child: Text('저장'),
            ),
          ],
        ),
      ),
    );
  }
}