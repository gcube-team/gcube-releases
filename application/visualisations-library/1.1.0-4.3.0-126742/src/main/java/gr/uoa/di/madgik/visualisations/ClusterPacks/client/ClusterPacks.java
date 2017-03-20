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
package gr.uoa.di.madgik.visualisations.ClusterPacks.client;

import gr.uoa.di.madgik.visualisations.client.injectors.CssResources;
import gr.uoa.di.madgik.visualisations.client.injectors.JSInjector;
import gr.uoa.di.madgik.visualisations.client.injectors.JsResources;

import com.google.gwt.core.client.EntryPoint;
import com.google.gwt.dom.client.StyleInjector;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.RootPanel;

/**
 * Entry point classes define <code>onModuleLoad()</code>.
 */
public class ClusterPacks implements EntryPoint {

	public void onModuleLoad() {
		StyleInjector.inject(CssResources.INSTANCE.clusterpacksCSS().getText());
		JSInjector.inject(JsResources.INSTANCE.d3olderJS().getText());
		JSInjector.inject(JsResources.INSTANCE.d3clust_layoutJS().getText());
		JSInjector.inject(JsResources.INSTANCE.clusterpacksJS().getText());
	}
	
	
	public void visualiseClusters(String divID, String dataJSON, String width, String height) {
		consoleLog("dataJSON: "+dataJSON);
		visualiseClustersJS(divID, dataJSON, width, height);
	}
	
	private static native void visualiseClustersJS(String divID, String dataJSON, String width, String height) /*-{
		$wnd.clusterPackVis(divID, dataJSON,  width, height);
	}-*/;
	
	
	private native void consoleLog( String message) /*-{
	    console.log(message );
	}-*/;
	
	
	
	
}
