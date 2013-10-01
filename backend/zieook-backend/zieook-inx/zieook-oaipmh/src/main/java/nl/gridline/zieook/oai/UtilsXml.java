/**
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 */
package nl.gridline.zieook.oai;

/**
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements. See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership. The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied. See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */

import java.util.LinkedList;
import java.util.List;

import org.w3c.dom.Element;
import org.w3c.dom.Node;

/**
 * Few simple utils to read DOM. This is originally from the Jakarta Commons
 * Modeler.
 * @author Costin Manolache
 */
public class UtilsXml
{

	/**
	 * @param elem
	 * @param tagName
	 * @return
	 */
	public static List<Element> findAllElementsByTagName(Element elem, String tagName)
	{
		List<Element> ret = new LinkedList<Element>();
		findAllElementsByTagName(elem, tagName, ret);
		return ret;
	}

	/**
	 * @param el
	 * @param tagName
	 * @param elementList
	 */
	private static void findAllElementsByTagName(Element el, String tagName, List<Element> elementList)
	{

		if (tagName.equals(el.getTagName()))
		{
			elementList.add(el);
		}
		Element elem = getFirstElement(el);
		while (elem != null)
		{
			findAllElementsByTagName(elem, tagName, elementList);
			elem = getNextElement(elem);
		}
	}

	/**
	 * @param parent
	 * @return
	 */
	public static Element getFirstElement(Node parent)
	{
		Node n = parent.getFirstChild();
		while (n != null && Node.ELEMENT_NODE != n.getNodeType())
		{
			n = n.getNextSibling();
		}
		if (n == null)
		{
			return null;
		}
		return (Element) n;
	}

	/**
	 * @param el
	 * @return
	 */
	public static Element getNextElement(Element el)
	{
		Node nd = el.getNextSibling();
		while (nd != null)
		{
			if (nd.getNodeType() == Node.ELEMENT_NODE)
			{
				return (Element) nd;
			}
			nd = nd.getNextSibling();
		}
		return null;
	}

}
