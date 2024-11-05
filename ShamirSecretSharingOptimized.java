import java.io.FileReader;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.io.BufferedReader;

public class ShamirSecretSharingOptimized {

	static class Point {
		BigInteger x, y;

		Point(BigInteger x, BigInteger y) {
			this.x = x;
			this.y = y;
		}
	}

	public static void main(String[] args) {
		try {
			
			Map<String, Object> input = parseJSONFile("input.json");

			
			Map<String, Object> keys = (Map<String, Object>) input.get("keys");
			int n = (int) keys.get("n");
			int k = (int) keys.get("k");
			int degree = k - 1;

			
			List<Point> points = new ArrayList<>();
			for (String key : input.keySet()) {
				if (!key.equals("keys")) {
					Map<String, String> root = (Map<String, String>) input.get(key);
					BigInteger x = new BigInteger(key);  // x is the key
					int base = Integer.parseInt(root.get("base"));
					String value = root.get("value");
					BigInteger y = new BigInteger(value, base);  
					points.add(new Point(x, y));
				}
			}

			
			BigInteger constantTerm = lagrangeInterpolation(points, degree);

			
			System.out.println("Secret (c) for the given polynomial: " + constantTerm);

		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	
	private static Map<String, Object> parseJSONFile(String fileName) throws Exception {
		BufferedReader reader = new BufferedReader(new FileReader(fileName));
		StringBuilder jsonBuilder = new StringBuilder();
		String line;

		while ((line = reader.readLine()) != null) {
			jsonBuilder.append(line.trim());
		}
		reader.close();

		
		return parseJSONObject(jsonBuilder.toString());
	}

	
	private static Map<String, Object> parseJSONObject(String json) {
		Map<String, Object> map = new HashMap<>();

		// Strip outer braces
		json = json.substring(1, json.length() - 1).trim();

		// Split by comma and process each part
		for (String part : json.split(",(?=\\s*\"\\w+\":)")) {
			String[] keyValue = part.split(":", 2);
			String key = keyValue[0].trim().replaceAll("\"", "");

			String value = keyValue[1].trim();
			if (value.startsWith("{")) {
				map.put(key, parseJSONObject(value));
			} else if (value.startsWith("\"")) {
				map.put(key, value.replaceAll("\"", ""));
			} else {
				map.put(key, Integer.parseInt(value));
			}
		}

		return map;
	}

	// Optimized Lagrange Interpolation to find the constant term c
	private static BigInteger lagrangeInterpolation(List<Point> points, int degree) {
		BigInteger result = BigInteger.ZERO;

		// Compute the constant term c using Lagrange Interpolation at x = 0
		for (int i = 0; i <= degree; i++) {
			BigInteger xi = points.get(i).x;
			BigInteger yi = points.get(i).y;

			BigInteger numerator = BigInteger.ONE;
			BigInteger denominator = BigInteger.ONE;

			for (int j = 0; j <= degree; j++) {
				if (i != j) {
					BigInteger xj = points.get(j).x;
					numerator = numerator.multiply(xj);  // L_i(0) product
					denominator = denominator.multiply(xj.subtract(xi));  // L_i denominator
				}
			}

			
			BigInteger term = yi.multiply(numerator).divide(denominator);
			result = result.add(term);
		}

		return result;
	}
}
