/*******************************************************************************
 * Copyright 2011 Google Inc. All Rights Reserved.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *******************************************************************************/
package gr.uoa.di.madgik.visualisations.client;

import gr.uoa.di.madgik.visualisations.client.injectors.CssResources;
import gr.uoa.di.madgik.visualisations.client.injectors.JsResources;
import gr.uoa.di.madgik.visualisations.client.injectors.JSInjector;

import com.google.gwt.core.client.EntryPoint;
import com.google.gwt.dom.client.StyleInjector;

/**
 * Entry point classes define <code>onModuleLoad()</code>.
 */
public class Visualizer implements EntryPoint {
	
	public void onModuleLoad() {
//		RootPanel rootPanel = RootPanel.get();
		
//		rootPanel.add(new ButtonVisualisation());
//		Collisions.alert("Running Visualizer class");
//		GWT.create(Collisions.class);
		
		JSInjector.inject(JsResources.INSTANCE.d3JS().getText());
		JSInjector.inject(JsResources.INSTANCE.jqueryJS().getText());
		JSInjector.inject(JsResources.INSTANCE.nvd3JS().getText());
		JSInjector.inject(JsResources.INSTANCE.jqueryjsonJS().getText());
		
		StyleInjector.inject(CssResources.INSTANCE.nvd3CSS().getText());	
		
		
	}
}
