/*
 * Copyright (c) 2014-2016 s1mpl3x <jan[at]over9000.eu>
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */

package eu.over9000.skadi.ui;

import org.junit.Test;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

/**
 * Created by Jan on 21.08.2015.
 */
public class BoolCompTest {

	@Test
	public void testComp() {
		final Comparator<Boolean> comp = (o1, o2) -> {

			if (o1 == null && o2 == null) {
				return 0;
			} else if (o1 != null && o2 == null) {
				return 1;
			} else if (o1 == null && o2 != null) {
				return -1;
			} else {
				return Boolean.compare(o1, o2);
			}
		};


		final List<Boolean> list = new ArrayList<>();
		list.add(null);
		list.add(Boolean.TRUE);
		list.add(Boolean.FALSE);
		list.add(Boolean.FALSE);
		list.add(Boolean.TRUE);
		list.add(null);

		Collections.sort(list, comp);

		System.out.println(list);
	}
}
