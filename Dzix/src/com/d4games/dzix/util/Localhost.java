/*
    Copyright 2011, Strategic Gains, Inc.

	Licensed under the Apache License, Version 2.0 (the "License");
	you may not use this file except in compliance with the License.
	You may obtain a copy of the License at

		http://www.apache.org/licenses/LICENSE-2.0

	Unless required by applicable law or agreed to in writing, software
	distributed under the License is distributed on an "AS IS" BASIS,
	WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
	See the License for the specific language governing permissions and
	limitations under the License.
 */
package com.d4games.dzix.util;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.UnknownHostException;

public enum Localhost {
	INSTANCE;

	private static String hostname;

	public static String getHostname() throws UnknownHostException {
		if (hostname == null) {
			int exitValue = 0;
			try {
				Process process = Runtime.getRuntime().exec("hostname");
				BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));
				hostname = reader.readLine();
				exitValue = process.waitFor();
			} catch (Exception e) {
				throw new UnknownHostException("cannot read hostname");
			}
			if (exitValue != 0) {
				throw new UnknownHostException("cannot read hostname");
			}
		}

		return hostname;
	}
}
