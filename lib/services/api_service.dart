import 'dart:convert';
import 'package:http/http.dart' as http;
import 'package:photochooser_flutter/utils/constants.dart';

class ApiService {
  static Future<String?> getRecommendation(String image1, String image2) async {
    final url = Uri.parse('${Constants.baseUrl}/v1/chat/completions');
    final headers = {
      'Authorization': 'Bearer ${Constants.apiKey}',
      'Content-Type': 'application/json',
    };

    final body = jsonEncode({
      'model': 'gpt-4o-mini',
      'messages': [
        {
          'role': 'system',
          'content': Constants.instaRecommend,
        },
        {
          'role': 'user',
          'content': 'Image 1: $image1\nImage 2: $image2',
          // 'content': [
          //   {'type': 'text', 'text': "image1, 2 are here!"},
          //   {'type': 'image_url', 'image_url': {'url': image1}, 'alt_text': 'image1'},
          //   {'type': 'image_url', 'image_url': {'url': image2}, 'alt_text': 'image2'},
          // ],
        },
      ],
      'temperature': Constants.temperature,
      'max_tokens': Constants.maxTokens,
    });

    final response = await http.post(url, headers: headers, body: body);

    if (response.statusCode == 200) {
      final data = jsonDecode(response.body);
      String resultText = data['choices'][0]['message']['content'];
      return resultText;
    } else {
      // 오류 처리
      print('Request : ${response.request}');
      print('Request failed: ${response.statusCode}');
      return null;
    }
  }
}