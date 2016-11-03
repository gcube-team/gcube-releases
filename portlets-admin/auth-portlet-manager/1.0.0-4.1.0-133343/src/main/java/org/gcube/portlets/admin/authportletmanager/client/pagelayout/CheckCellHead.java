package org.gcube.portlets.admin.authportletmanager.client.pagelayout;
import com.google.gwt.cell.client.AbstractCell;
import com.google.gwt.safehtml.shared.SafeHtml;
import com.google.gwt.safehtml.shared.SafeHtmlBuilder;
import com.google.gwt.safehtml.shared.SafeHtmlUtils;

/**
  *
  * @author SISTEMAS
  */
public class CheckCellHead extends AbstractCell<Boolean> {

     private Boolean isSelected = false;
     private static final SafeHtml INPUT_CHECKED =
SafeHtmlUtils.fromSafeConstant("<input type=\"checkbox\" tabindex=\"-1\"checked/>");
     private static final SafeHtml INPUT_UNCHECKED =
SafeHtmlUtils.fromSafeConstant("<input type=\"checkbox\"tabindex=\"-1\"/>");

     public CheckCellHead() {
         super("click");
     }

     @Override
     public void render(Context context, Boolean value, SafeHtmlBuilder sb) {
         if (isSelected) {
             sb.append(INPUT_CHECKED);
         } else {
             sb.append(INPUT_UNCHECKED);
         }
     }

     public Boolean isIsSelected() {
         return isSelected;
     }

     public void setIsSelected(Boolean isSelected) {
         this.isSelected = isSelected;
     }



}
