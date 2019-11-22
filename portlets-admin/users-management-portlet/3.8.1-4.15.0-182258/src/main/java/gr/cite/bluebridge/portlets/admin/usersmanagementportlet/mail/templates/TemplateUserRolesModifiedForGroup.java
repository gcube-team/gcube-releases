package gr.cite.bluebridge.portlets.admin.usersmanagementportlet.mail.templates;

import org.gcube.common.portal.GCubePortalConstants;
import org.gcube.common.portal.PortalContext;
import org.gcube.common.portal.mailing.templates.AbstractTemplate;
import org.gcube.common.portal.mailing.templates.Template;
import org.gcube.portal.mailing.message.Constants;
import org.gcube.vomanagement.usermanagement.model.*;

import java.util.Base64;
import java.util.Collection;
import java.util.Date;
import java.util.List;

public class TemplateUserRolesModifiedForGroup extends AbstractTemplate implements Template {
    private enum FormatType{
        TEXT, HTML
    }
    private final String hiddenSection = "none";
    private final String visibleSection = "block";
    private final static String encodedTemplateHTML = "PCFET0NUWVBFIGh0bWwgUFVCTElDICItLy9XM0MvL0RURCBYSFRNTCAxLjAgU3RyaWN0Ly9FTiIgImh0dHA6Ly93d3cudzMub3JnL1RSL3hodG1sMS9EVEQveGh0bWwxLXN0cmljdC5kdGQiPgo8aHRtbCB4bWxucz0iaHR0cDovL3d3dy53My5vcmcvMTk5OS94aHRtbCIgeG1sbnM9Imh0dHA6Ly93d3cudzMub3JnLzE5OTkveGh0bWwiIHN0eWxlPSJtaW4taGVpZ2h0OiAxMDAlOyBiYWNrZ3JvdW5kLWNvbG9yOiAjZjNmM2YzICFpbXBvcnRhbnQ7Ij4KICA8aGVhZD4KICAgIDxtZXRhIGh0dHAtZXF1aXY9IkNvbnRlbnQtVHlwZSIgY29udGVudD0idGV4dC9odG1sOyBjaGFyc2V0PXV0Zi04IiAvPgogICAgPG1ldGEgbmFtZT0idmlld3BvcnQiIGNvbnRlbnQ9IndpZHRoPWRldmljZS13aWR0aCIgLz4KICAgIDx0aXRsZT5UaXRsZTwvdGl0bGU+CiAgPC9oZWFkPgoKPGJvZHkgc3R5bGU9IndpZHRoOiAxMDAlICFpbXBvcnRhbnQ7IG1pbi13aWR0aDogMTAwJTsgLXdlYmtpdC10ZXh0LXNpemUtYWRqdXN0OiAxMDAlOyAtbXMtdGV4dC1zaXplLWFkanVzdDogMTAwJTsgLW1vei1ib3gtc2l6aW5nOiBib3JkZXItYm94OyAtd2Via2l0LWJveC1zaXppbmc6IGJvcmRlci1ib3g7IGJveC1zaXppbmc6IGJvcmRlci1ib3g7IGNvbG9yOiAjMGEwYTBhOyBmb250LWZhbWlseTogSGVsdmV0aWNhLCBBcmlhbCwgc2Fucy1zZXJpZjsgZm9udC13ZWlnaHQ6IG5vcm1hbDsgdGV4dC1hbGlnbjogbGVmdDsgbGluZS1oZWlnaHQ6IDEuMzsgZm9udC1zaXplOiAxNnB4OyBiYWNrZ3JvdW5kLWNvbG9yOiAjZjNmM2YzICFpbXBvcnRhbnQ7IG1hcmdpbjogMDsgcGFkZGluZzogMDsiIGJnY29sb3I9IiNmM2YzZjMgIWltcG9ydGFudCI+ICAKICA8dGFibGUgY2xhc3M9ImJvZHkiIGRhdGEtbWFkZS13aXRoLWZvdW5kYXRpb249IiIgc3R5bGU9ImJvcmRlci1zcGFjaW5nOiAwOyBib3JkZXItY29sbGFwc2U6IGNvbGxhcHNlOyB2ZXJ0aWNhbC1hbGlnbjogdG9wOyB0ZXh0LWFsaWduOiBsZWZ0OyBiYWNrZ3JvdW5kLWNvbG9yOiAjZjNmM2YzICFpbXBvcnRhbnQ7IGhlaWdodDogMTAwJTsgd2lkdGg6IDEwMCU7IGNvbG9yOiAjMGEwYTBhOyBmb250LWZhbWlseTogSGVsdmV0aWNhLCBBcmlhbCwgc2Fucy1zZXJpZjsgZm9udC13ZWlnaHQ6IG5vcm1hbDsgbGluZS1oZWlnaHQ6IDEuMzsgZm9udC1zaXplOiAxNnB4OyBtYXJnaW46IDA7IHBhZGRpbmc6IDA7IiBiZ2NvbG9yPSIjZjNmM2YzICFpbXBvcnRhbnQiPjx0Ym9keT48dHIgc3R5bGU9InZlcnRpY2FsLWFsaWduOiB0b3A7IHRleHQtYWxpZ246IGxlZnQ7IHBhZGRpbmc6IDA7IiBhbGlnbj0ibGVmdCI+PHRkIGNsYXNzPSJmbG9hdC1jZW50ZXIiIGFsaWduPSJjZW50ZXIiIHZhbGlnbj0idG9wIiBzdHlsZT0id29yZC13cmFwOiBicmVhay13b3JkOyAtd2Via2l0LWh5cGhlbnM6IGF1dG87IC1tb3otaHlwaGVuczogYXV0bzsgaHlwaGVuczogYXV0bzsgYm9yZGVyLWNvbGxhcHNlOiBjb2xsYXBzZSAhaW1wb3J0YW50OyB2ZXJ0aWNhbC1hbGlnbjogdG9wOyB0ZXh0LWFsaWduOiBjZW50ZXI7IGZsb2F0OiBub25lOyBjb2xvcjogIzBhMGEwYTsgZm9udC1mYW1pbHk6IEhlbHZldGljYSwgQXJpYWwsIHNhbnMtc2VyaWY7IGZvbnQtd2VpZ2h0OiBub3JtYWw7IGxpbmUtaGVpZ2h0OiAxLjM7IGZvbnQtc2l6ZTogMTZweDsgbWFyZ2luOiAwIGF1dG87IHBhZGRpbmc6IDA7Ij4KICAgICAgICAgIDxjZW50ZXIgZGF0YS1wYXJzZWQ9IiIgc3R5bGU9IndpZHRoOiAxMDAlOyBtaW4td2lkdGg6IDU4MHB4OyI+CiAgICAgICAgICAgIDx0YWJsZSBhbGlnbj0iY2VudGVyIiBjbGFzcz0id3JhcHBlciBoZWFkZXIgZmxvYXQtY2VudGVyIiBzdHlsZT0id2lkdGg6IDEwMCU7IGJvcmRlci1zcGFjaW5nOiAwOyBib3JkZXItY29sbGFwc2U6IGNvbGxhcHNlOyB2ZXJ0aWNhbC1hbGlnbjogdG9wOyB0ZXh0LWFsaWduOiBjZW50ZXI7IGZsb2F0OiBub25lOyBtYXJnaW46IDAgYXV0bzsgcGFkZGluZzogMDsiPjx0Ym9keT48dHIgc3R5bGU9InZlcnRpY2FsLWFsaWduOiB0b3A7IHRleHQtYWxpZ246IGxlZnQ7IHBhZGRpbmc6IDA7IiBhbGlnbj0ibGVmdCI+PHRkIGNsYXNzPSJ3cmFwcGVyLWlubmVyIiBzdHlsZT0id29yZC13cmFwOiBicmVhay13b3JkOyAtd2Via2l0LWh5cGhlbnM6IGF1dG87IC1tb3otaHlwaGVuczogYXV0bzsgaHlwaGVuczogYXV0bzsgYm9yZGVyLWNvbGxhcHNlOiBjb2xsYXBzZSAhaW1wb3J0YW50OyB2ZXJ0aWNhbC1hbGlnbjogdG9wOyB0ZXh0LWFsaWduOiBsZWZ0OyBjb2xvcjogIzBhMGEwYTsgZm9udC1mYW1pbHk6IEhlbHZldGljYSwgQXJpYWwsIHNhbnMtc2VyaWY7IGZvbnQtd2VpZ2h0OiBub3JtYWw7IGxpbmUtaGVpZ2h0OiAxLjM7IGZvbnQtc2l6ZTogMTZweDsgbWFyZ2luOiAwOyBwYWRkaW5nOiAwOyIgYWxpZ249ImxlZnQiIHZhbGlnbj0idG9wIj4KICAgICAgICAgICAgICAgICAgPHRhYmxlIGFsaWduPSJjZW50ZXIiIGNsYXNzPSJjb250YWluZXIiIHN0eWxlPSJib3JkZXItc3BhY2luZzogMDsgYm9yZGVyLWNvbGxhcHNlOiBjb2xsYXBzZTsgdmVydGljYWwtYWxpZ246IHRvcDsgdGV4dC1hbGlnbjogaW5oZXJpdDsgd2lkdGg6IDU4MHB4OyBiYWNrZ3JvdW5kOiAjZmVmZWZlOyBtYXJnaW46IDAgYXV0bzsgcGFkZGluZzogMDsiIGJnY29sb3I9IiNmZWZlZmUiPjx0Ym9keT48dHIgc3R5bGU9InZlcnRpY2FsLWFsaWduOiB0b3A7IHRleHQtYWxpZ246IGxlZnQ7IHBhZGRpbmc6IDA7IiBhbGlnbj0ibGVmdCI+PHRkIHN0eWxlPSJ3b3JkLXdyYXA6IGJyZWFrLXdvcmQ7IC13ZWJraXQtaHlwaGVuczogYXV0bzsgLW1vei1oeXBoZW5zOiBhdXRvOyBoeXBoZW5zOiBhdXRvOyBib3JkZXItY29sbGFwc2U6IGNvbGxhcHNlICFpbXBvcnRhbnQ7IHZlcnRpY2FsLWFsaWduOiB0b3A7IHRleHQtYWxpZ246IGxlZnQ7IGNvbG9yOiAjMGEwYTBhOyBmb250LWZhbWlseTogSGVsdmV0aWNhLCBBcmlhbCwgc2Fucy1zZXJpZjsgZm9udC13ZWlnaHQ6IG5vcm1hbDsgbGluZS1oZWlnaHQ6IDEuMzsgZm9udC1zaXplOiAxNnB4OyBtYXJnaW46IDA7IHBhZGRpbmc6IDA7IiBhbGlnbj0ibGVmdCIgdmFsaWduPSJ0b3AiPgogICAgICAgICAgICAgICAgICAgICAgICAgIDx0YWJsZSBjbGFzcz0icm93IGNvbGxhcHNlIiBzdHlsZT0iYm9yZGVyLXNwYWNpbmc6IDA7IGJvcmRlci1jb2xsYXBzZTogY29sbGFwc2U7IHZlcnRpY2FsLWFsaWduOiB0b3A7IHRleHQtYWxpZ246IGxlZnQ7IHdpZHRoOiAxMDAlOyBwb3NpdGlvbjogcmVsYXRpdmU7IGRpc3BsYXk6IHRhYmxlOyBwYWRkaW5nOiAwOyI+PHRib2R5Pjx0ciBzdHlsZT0idmVydGljYWwtYWxpZ246IHRvcDsgdGV4dC1hbGlnbjogbGVmdDsgcGFkZGluZzogMDsiIGFsaWduPSJsZWZ0Ij48dGggY2xhc3M9InNtYWxsLTYgbGFyZ2UtNiBjb2x1bW5zIGZpcnN0IiBzdHlsZT0id2lkdGg6IDI5OHB4OyBjb2xvcjogIzBhMGEwYTsgZm9udC1mYW1pbHk6IEhlbHZldGljYSwgQXJpYWwsIHNhbnMtc2VyaWY7IGZvbnQtd2VpZ2h0OiBub3JtYWw7IHRleHQtYWxpZ246IGxlZnQ7IGxpbmUtaGVpZ2h0OiAxLjM7IGZvbnQtc2l6ZTogMTZweDsgbWFyZ2luOiAwIGF1dG87IHBhZGRpbmc6IDAgMCAxNnB4OyIgYWxpZ249ImxlZnQiPgogICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgPHRhYmxlIHN0eWxlPSJib3JkZXItc3BhY2luZzogMDsgYm9yZGVyLWNvbGxhcHNlOiBjb2xsYXBzZTsgdmVydGljYWwtYWxpZ246IHRvcDsgdGV4dC1hbGlnbjogbGVmdDsgd2lkdGg6IDEwMCU7IHBhZGRpbmc6IDA7Ij48dGJvZHk+PHRyIHN0eWxlPSJ2ZXJ0aWNhbC1hbGlnbjogdG9wOyB0ZXh0LWFsaWduOiBsZWZ0OyBwYWRkaW5nOiAwOyIgYWxpZ249ImxlZnQiPjx0aCBzdHlsZT0iY29sb3I6ICMwYTBhMGE7IGZvbnQtZmFtaWx5OiBIZWx2ZXRpY2EsIEFyaWFsLCBzYW5zLXNlcmlmOyBmb250LXdlaWdodDogbm9ybWFsOyB0ZXh0LWFsaWduOiBsZWZ0OyBsaW5lLWhlaWdodDogMS4zOyBmb250LXNpemU6IDE2cHg7IG1hcmdpbjogMDsgcGFkZGluZzogMDsiIGFsaWduPSJsZWZ0Ij4gPGltZyBzcmM9Int7R0FURVdBWV9MT0dPOlVSTH19IiBzdHlsZT0id2lkdGg6IDIwMHB4OyBvdXRsaW5lOiBub25lOyB0ZXh0LWRlY29yYXRpb246IG5vbmU7IC1tcy1pbnRlcnBvbGF0aW9uLW1vZGU6IGJpY3ViaWM7IG1heC13aWR0aDogMTAwJTsgY2xlYXI6IGJvdGg7IGRpc3BsYXk6IGJsb2NrOyIgYWx0PSJ7e0dBVEVXQVlfTkFNRX19IiB0aXRsZT0ie3tHQVRFV0FZX05BTUV9fSI+PC90aD4KICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgPC90cj48L3Rib2R5PjwvdGFibGU+PC90aD4KICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICA8dGggY2xhc3M9InNtYWxsLTYgbGFyZ2UtNiBjb2x1bW5zIGxhc3QiIHN0eWxlPSJ3aWR0aDogMjk4cHg7IGNvbG9yOiAjMGEwYTBhOyBmb250LWZhbWlseTogSGVsdmV0aWNhLCBBcmlhbCwgc2Fucy1zZXJpZjsgZm9udC13ZWlnaHQ6IG5vcm1hbDsgdGV4dC1hbGlnbjogbGVmdDsgbGluZS1oZWlnaHQ6IDEuMzsgZm9udC1zaXplOiAxNnB4OyBtYXJnaW46IDAgYXV0bzsgcGFkZGluZzogMCAwIDE2cHg7IiBhbGlnbj0ibGVmdCI+CiAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICA8dGFibGUgc3R5bGU9ImJvcmRlci1zcGFjaW5nOiAwOyBib3JkZXItY29sbGFwc2U6IGNvbGxhcHNlOyB2ZXJ0aWNhbC1hbGlnbjogdG9wOyB0ZXh0LWFsaWduOiBsZWZ0OyB3aWR0aDogMTAwJTsgcGFkZGluZzogMDsiPjx0Ym9keT48dHIgc3R5bGU9InZlcnRpY2FsLWFsaWduOiB0b3A7IHRleHQtYWxpZ246IGxlZnQ7IHBhZGRpbmc6IDA7IiBhbGlnbj0ibGVmdCI+PHRoIHN0eWxlPSJjb2xvcjogIzBhMGEwYTsgZm9udC1mYW1pbHk6IEhlbHZldGljYSwgQXJpYWwsIHNhbnMtc2VyaWY7IGZvbnQtd2VpZ2h0OiBub3JtYWw7IHRleHQtYWxpZ246IGxlZnQ7IGxpbmUtaGVpZ2h0OiAxLjM7IGZvbnQtc2l6ZTogMTZweDsgbWFyZ2luOiAwOyBwYWRkaW5nOiAwOyIgYWxpZ249ImxlZnQiPgogICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgPHAgY2xhc3M9InRleHQtcmlnaHQiIHN0eWxlPSJ0ZXh0LWFsaWduOiByaWdodDsgY29sb3I6ICMwYTBhMGE7IGZvbnQtZmFtaWx5OiBIZWx2ZXRpY2EsIEFyaWFsLCBzYW5zLXNlcmlmOyBmb250LXdlaWdodDogbm9ybWFsOyBsaW5lLWhlaWdodDogMS4zOyBmb250LXNpemU6IDE2cHg7IG1hcmdpbjogMCAwIDEwcHg7IHBhZGRpbmc6IDA7IiBhbGlnbj0icmlnaHQiPjwvcD4KICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICA8L3RoPgogICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICA8L3RyPjwvdGJvZHk+PC90YWJsZT48L3RoPgogICAgICAgICAgICAgICAgICAgICAgICAgICAgICA8L3RyPjwvdGJvZHk+PC90YWJsZT48L3RkPgogICAgICAgICAgICAgICAgICAgICAgPC90cj48L3Rib2R5PjwvdGFibGU+PC90ZD4KICAgICAgICAgICAgICA8L3RyPjwvdGJvZHk+PC90YWJsZT48dGFibGUgYWxpZ249ImNlbnRlciIgY2xhc3M9ImNvbnRhaW5lciBib2R5LWJvcmRlciBmbG9hdC1jZW50ZXIiIHN0eWxlPSJib3JkZXItc3BhY2luZzogMDsgYm9yZGVyLWNvbGxhcHNlOiBjb2xsYXBzZTsgdmVydGljYWwtYWxpZ246IHRvcDsgdGV4dC1hbGlnbjogY2VudGVyOyB3aWR0aDogNTgwcHg7IGZsb2F0OiBub25lOyBib3JkZXItdG9wLXdpZHRoOiA4cHg7IGJvcmRlci10b3AtY29sb3I6ICMyMjVmOTc7IGJvcmRlci10b3Atc3R5bGU6IHNvbGlkOyBiYWNrZ3JvdW5kOiAjZmVmZWZlOyBtYXJnaW46IDAgYXV0bzsgcGFkZGluZzogMDsiIGJnY29sb3I9IiNmZWZlZmUiPjx0Ym9keT48dHIgc3R5bGU9InZlcnRpY2FsLWFsaWduOiB0b3A7IHRleHQtYWxpZ246IGxlZnQ7IHBhZGRpbmc6IDA7IiBhbGlnbj0ibGVmdCI+PHRkIHN0eWxlPSJ3b3JkLXdyYXA6IGJyZWFrLXdvcmQ7IC13ZWJraXQtaHlwaGVuczogYXV0bzsgLW1vei1oeXBoZW5zOiBhdXRvOyBoeXBoZW5zOiBhdXRvOyBib3JkZXItY29sbGFwc2U6IGNvbGxhcHNlICFpbXBvcnRhbnQ7IHZlcnRpY2FsLWFsaWduOiB0b3A7IHRleHQtYWxpZ246IGxlZnQ7IGNvbG9yOiAjMGEwYTBhOyBmb250LWZhbWlseTogSGVsdmV0aWNhLCBBcmlhbCwgc2Fucy1zZXJpZjsgZm9udC13ZWlnaHQ6IG5vcm1hbDsgbGluZS1oZWlnaHQ6IDEuMzsgZm9udC1zaXplOiAxNnB4OyBtYXJnaW46IDA7IHBhZGRpbmc6IDA7IiBhbGlnbj0ibGVmdCIgdmFsaWduPSJ0b3AiPgogICAgICAgICAgICAgICAgICAgIDx0YWJsZSBjbGFzcz0icm93IiBzdHlsZT0iYm9yZGVyLXNwYWNpbmc6IDA7IGJvcmRlci1jb2xsYXBzZTogY29sbGFwc2U7IHZlcnRpY2FsLWFsaWduOiB0b3A7IHRleHQtYWxpZ246IGxlZnQ7IHdpZHRoOiAxMDAlOyBwb3NpdGlvbjogcmVsYXRpdmU7IGRpc3BsYXk6IHRhYmxlOyBwYWRkaW5nOiAwOyI+PHRib2R5Pjx0ciBzdHlsZT0idmVydGljYWwtYWxpZ246IHRvcDsgdGV4dC1hbGlnbjogbGVmdDsgcGFkZGluZzogMDsiIGFsaWduPSJsZWZ0Ij48dGggY2xhc3M9InNtYWxsLTEyIGxhcmdlLTEyIGNvbHVtbnMgZmlyc3QgbGFzdCIgc3R5bGU9IndpZHRoOiA1NjRweDsgY29sb3I6ICMwYTBhMGE7IGZvbnQtZmFtaWx5OiBIZWx2ZXRpY2EsIEFyaWFsLCBzYW5zLXNlcmlmOyBmb250LXdlaWdodDogbm9ybWFsOyB0ZXh0LWFsaWduOiBsZWZ0OyBsaW5lLWhlaWdodDogMS4zOyBmb250LXNpemU6IDE2cHg7IG1hcmdpbjogMCBhdXRvOyBwYWRkaW5nOiAwIDE2cHggMTZweDsiIGFsaWduPSJsZWZ0Ij4KICAgICAgICAgICAgICAgICAgICAgICAgICAgIDx0YWJsZSBzdHlsZT0iYm9yZGVyLXNwYWNpbmc6IDA7IGJvcmRlci1jb2xsYXBzZTogY29sbGFwc2U7IHZlcnRpY2FsLWFsaWduOiB0b3A7IHRleHQtYWxpZ246IGxlZnQ7IHdpZHRoOiAxMDAlOyBwYWRkaW5nOiAwOyI+PHRib2R5Pjx0ciBzdHlsZT0idmVydGljYWwtYWxpZ246IHRvcDsgdGV4dC1hbGlnbjogbGVmdDsgcGFkZGluZzogMDsiIGFsaWduPSJsZWZ0Ij48dGggc3R5bGU9ImNvbG9yOiAjMGEwYTBhOyBmb250LWZhbWlseTogSGVsdmV0aWNhLCBBcmlhbCwgc2Fucy1zZXJpZjsgZm9udC13ZWlnaHQ6IG5vcm1hbDsgdGV4dC1hbGlnbjogbGVmdDsgbGluZS1oZWlnaHQ6IDEuMzsgZm9udC1zaXplOiAxNnB4OyBtYXJnaW46IDA7IHBhZGRpbmc6IDA7IiBhbGlnbj0ibGVmdCI+CiAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICA8dGFibGUgY2xhc3M9InNwYWNlciIgc3R5bGU9ImJvcmRlci1zcGFjaW5nOiAwOyBib3JkZXItY29sbGFwc2U6IGNvbGxhcHNlOyB2ZXJ0aWNhbC1hbGlnbjogdG9wOyB0ZXh0LWFsaWduOiBsZWZ0OyB3aWR0aDogMTAwJTsgcGFkZGluZzogMDsiPjx0Ym9keT48dHIgc3R5bGU9InZlcnRpY2FsLWFsaWduOiB0b3A7IHRleHQtYWxpZ246IGxlZnQ7IHBhZGRpbmc6IDA7IiBhbGlnbj0ibGVmdCI+PHRkIGhlaWdodD0iMTZweCIgc3R5bGU9ImZvbnQtc2l6ZTogMTZweDsgbGluZS1oZWlnaHQ6IDE2cHg7IHdvcmQtd3JhcDogYnJlYWstd29yZDsgLXdlYmtpdC1oeXBoZW5zOiBhdXRvOyAtbW96LWh5cGhlbnM6IGF1dG87IGh5cGhlbnM6IGF1dG87IGJvcmRlci1jb2xsYXBzZTogY29sbGFwc2UgIWltcG9ydGFudDsgdmVydGljYWwtYWxpZ246IHRvcDsgdGV4dC1hbGlnbjogbGVmdDsgbXNvLWxpbmUtaGVpZ2h0LXJ1bGU6IGV4YWN0bHk7IGNvbG9yOiAjMGEwYTBhOyBmb250LWZhbWlseTogSGVsdmV0aWNhLCBBcmlhbCwgc2Fucy1zZXJpZjsgZm9udC13ZWlnaHQ6IG5vcm1hbDsgbWFyZ2luOiAwOyBwYWRkaW5nOiAwOyIgYWxpZ249ImxlZnQiIHZhbGlnbj0idG9wIj4mbmJzcDs8L3RkPgogICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgIDwvdHI+PC90Ym9keT48L3RhYmxlPgoJCQkJCQkJCQkgIDxoNCBzdHlsZT0iY29sb3I6IGluaGVyaXQ7IGZvbnQtZmFtaWx5OiBIZWx2ZXRpY2EsIEFyaWFsLCBzYW5zLXNlcmlmOyBmb250LXdlaWdodDogbm9ybWFsOyB0ZXh0LWFsaWduOiBsZWZ0OyBsaW5lLWhlaWdodDogMS4zOyB3b3JkLXdyYXA6IG5vcm1hbDsgZm9udC1zaXplOiAyNHB4OyBtYXJnaW46IDAgMCAxMHB4OyBwYWRkaW5nOiAwOyIgYWxpZ249ImxlZnQiPgoJCQkJCQkJCQkJICBIaSB7e1JFUVVFU1RJTkdfVVNFUl9GSVJTVF9OQU1FfX0sCgkJCQkJCQkJCQkgIDxicj4KCQkJCQkJCQkJCSAgPHNwYW4gc3R5bGU9ImRpc3BsYXk6e3tWSVNJQklMSVRZX09GX05FV19ST0xFU19TRUNUSU9OfX07Ij48YSBocmVmPSJ7e1VTRVJfVlJFTUVNQkVSX1BST0ZJTEVfVVJMfX0iIHN0eWxlPSJjb2xvcjogIzIxOTllODsgZm9udC1mYW1pbHk6IEhlbHZldGljYSwgQXJpYWwsIHNhbnMtc2VyaWY7IGZvbnQtd2VpZ2h0OiBub3JtYWw7IHRleHQtYWxpZ246IGxlZnQ7IGxpbmUtaGVpZ2h0OiAxLjM7IHRleHQtZGVjb3JhdGlvbjogbm9uZTsgbWFyZ2luOiAwOyBwYWRkaW5nOiAwOyI+CgkJCQkJCQkJCQkgIHt7VVNFUl9GVUxMTkFNRX19CgkJCQkJCQkJCQkgIDwvYT4gaGFzIGp1c3QgYXNzaWduZWQgeW91IHRoZSBmb2xsb3dpbmcgcm9sZXM6IHt7TkVXX1JPTEVTfX0gaW4gdGhlIAoJCQkJCQkJCQkJICA8YSBocmVmPSJ7e1ZSRV9VUkx9fSIgc3R5bGU9ImNvbG9yOiAjMjE5OWU4OyBmb250LWZhbWlseTogSGVsdmV0aWNhLCBBcmlhbCwgc2Fucy1zZXJpZjsgZm9udC13ZWlnaHQ6IG5vcm1hbDsgdGV4dC1hbGlnbjogbGVmdDsgbGluZS1oZWlnaHQ6IDEuMzsgdGV4dC1kZWNvcmF0aW9uOiBub25lOyBtYXJnaW46IDA7IHBhZGRpbmc6IDA7Ij4KCQkJCQkJCQkJCSAge3tTRUxFQ1RFRF9WUkVfTkFNRX19CgkJCQkJCQkJCQkgIDwvYT4gVlJFLgo8L3NwYW4+CgkJCQkJCQkJCSAgPC9oND4KCQkJCQkJCQkJICA8aDQgc3R5bGU9ImNvbG9yOiBpbmhlcml0OyBkaXNwbGF5Ont7VklTSUJJTElUWV9PRl9SRVZPS0VEX1JPTEVTX1NFQ1RJT059fTsgZm9udC1mYW1pbHk6IEhlbHZldGljYSwgQXJpYWwsIHNhbnMtc2VyaWY7IGZvbnQtd2VpZ2h0OiBub3JtYWw7IHRleHQtYWxpZ246IGxlZnQ7IGxpbmUtaGVpZ2h0OiAxLjM7IHdvcmQtd3JhcDogbm9ybWFsOyBmb250LXNpemU6IDI0cHg7IG1hcmdpbjogMCAwIDEwcHg7IHBhZGRpbmc6IDA7IiBhbGlnbj0ibGVmdCI+CgkJCQkJCQkJCQkgIDxhIGhyZWY9Int7VVNFUl9WUkVNRU1CRVJfUFJPRklMRV9VUkx9fSIgc3R5bGU9ImNvbG9yOiAjMjE5OWU4OyBmb250LWZhbWlseTogSGVsdmV0aWNhLCBBcmlhbCwgc2Fucy1zZXJpZjsgZm9udC13ZWlnaHQ6IG5vcm1hbDsgdGV4dC1hbGlnbjogbGVmdDsgbGluZS1oZWlnaHQ6IDEuMzsgdGV4dC1kZWNvcmF0aW9uOiBub25lOyBtYXJnaW46IDA7IHBhZGRpbmc6IDA7Ij4KCQkJCQkJCQkJCSAge3tVU0VSX0ZVTExOQU1FfX0KCQkJCQkJCQkJCSAgPC9hPiBoYXMganVzdCByZXZva2VkIHlvdXIgcm9sZXM6IHt7UkVWT0tFRF9ST0xFU319IGluIHRoZSAKCQkJCQkJCQkJCSAgPGEgaHJlZj0ie3tWUkVfVVJMfX0iIHN0eWxlPSJjb2xvcjogIzIxOTllODsgZm9udC1mYW1pbHk6IEhlbHZldGljYSwgQXJpYWwsIHNhbnMtc2VyaWY7IGZvbnQtd2VpZ2h0OiBub3JtYWw7IHRleHQtYWxpZ246IGxlZnQ7IGxpbmUtaGVpZ2h0OiAxLjM7IHRleHQtZGVjb3JhdGlvbjogbm9uZTsgbWFyZ2luOiAwOyBwYWRkaW5nOiAwOyI+CgkJCQkJCQkJCQkgIHt7U0VMRUNURURfVlJFX05BTUV9fQoJCQkJCQkJCQkJICA8L2E+IFZSRS4KCgkJCQkJCQkJCSAgPC9oND4KCiAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgPC90aD4KICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICA8dGggY2xhc3M9ImV4cGFuZGVyIiBzdHlsZT0idmlzaWJpbGl0eTogaGlkZGVuOyB3aWR0aDogMDsgY29sb3I6ICMwYTBhMGE7IGZvbnQtZmFtaWx5OiBIZWx2ZXRpY2EsIEFyaWFsLCBzYW5zLXNlcmlmOyBmb250LXdlaWdodDogbm9ybWFsOyB0ZXh0LWFsaWduOiBsZWZ0OyBsaW5lLWhlaWdodDogMS4zOyBmb250LXNpemU6IDE2cHg7IG1hcmdpbjogMDsgcGFkZGluZzogMDsiIGFsaWduPSJsZWZ0Ij48L3RoPgogICAgICAgICAgICAgICAgICAgICAgICAgICAgICA8L3RyPjwvdGJvZHk+PC90YWJsZT48L3RoPgogICAgICAgICAgICAgICAgICAgICAgICA8L3RyPjwvdGJvZHk+PC90YWJsZT4KCjwvdGQ+CiAgICAgICAgICAgICAgICA8L3RyPjwvdGJvZHk+PC90YWJsZT48L2NlbnRlcj4KICAgICAgICA8L3RkPgogICAgICA8L3RyPjwvdGJvZHk+PC90YWJsZT4KPC9ib2R5PjwvaHRtbD4K";
    private final static String encodedTemplateTEXT = "e3tHQVRFV0FZX05BTUV9fQotLS0tLS0tLS0tLS0tLS0tLS0tLS0tLS0tLS0tLS0tLS0tCkhpIHt7UkVRVUVTVElOR19VU0VSX0ZJUlNUX05BTUV9fSwKCnt7VVNFUl9GVUxMTkFNRX19IGhhcyBqdXN0IGFzc2lnbmVkIHlvdSB0aGUgcm9sZXMge3tORVdfUk9MRVN9fSBpbiB0aGUge3tTRUxFQ1RFRF9WUkVfTkFNRX19IFZSRS4KCnt7VVNFUl9GVUxMTkFNRX19IGhhcyBqdXN0IHJldm9rZWQgeW91ciByb2xlcyB7e1JFVk9LRURfUk9MRVN9fSBpbiB0aGUge3tTRUxFQ1RFRF9WUkVfTkFNRX19IFZSRS4=";
    private final static String encodedTemplateTEXTGreeting = "e3tHQVRFV0FZX05BTUV9fQotLS0tLS0tLS0tLS0tLS0tLS0tLS0tLS0tLS0tLS0tLS0tCkhpIHt7UkVRVUVTVElOR19VU0VSX0ZJUlNUX05BTUV9fSwK";
    private final static String encodedTemplateTEXTAssignedRoles = "Cnt7VVNFUl9GVUxMTkFNRX19IGhhcyBqdXN0IGFzc2lnbmVkIHlvdSB0aGUgcm9sZXMge3tORVdfUk9MRVN9fSBpbiB0aGUge3tTRUxFQ1RFRF9WUkVfTkFNRX19IFZSRS4K";
    private final static String encodedTemplateTEXTRevokedRoles = "Cnt7VVNFUl9GVUxMTkFNRX19IGhhcyBqdXN0IHJldm9rZWQgeW91ciByb2xlcyB7e1JFVk9LRURfUk9MRVN9fSBpbiB0aGUge3tTRUxFQ1RFRF9WUkVfTkFNRX19IFZSRS4=";

    private FormatType formatType;

    private GCubeGroup theRequestedVRE;
    private GCubeUser theRequestingUser;
    private GCubeUser theManagerUser;
    private List<GCubeRole> newRoles;
    private List<GCubeRole> rolesRevoked;
    private Date originalRequestDate;
    private String vreURL;

    /**
     *
     * @param theRequestingUser an instance of @see {@link GCubeUser} representing the user who requested access
     * @param theManagerUser an instance of @see {@link GCubeUser} representing the manager who approved the request
     * @param theRequestedVRE instance of @see {@link GCubeGroup} of the current VRE
     * @param originalRequestDate the request date as in the associated {@link GCubeMembershipRequest}
     * @param gatewayName gateway name can be obtained with {@link PortalContext#getGatewayName(javax.servlet.http.HttpServletRequest)}
     * @param gatewayURL gateway URL name can be obtained with {@link PortalContext#getGatewayURL(javax.servlet.http.HttpServletRequest)}
     * @param newRoles a collection of instances of @see {@link GCubeRole} representing the collection of new roles the user will be assigned
     * @param rolesRevoked a collection of instances of @see {@link GCubeRole} representing the collection of roles that will be revoked
     */

    public TemplateUserRolesModifiedForGroup(String gatewayName, String gatewayURL, GCubeGroup theRequestedVRE,
                                             GCubeUser theRequestingUser, GCubeUser theManagerUser, List<GCubeRole> newRoles,
                                             List<GCubeRole> rolesRevoked, Date originalRequestDate) {
        super(gatewayName, gatewayURL);

        this.theRequestedVRE = theRequestedVRE;
        this.theRequestingUser = theRequestingUser;
        this.theManagerUser = theManagerUser;
        this.newRoles = newRoles;
        this.rolesRevoked = rolesRevoked;
        this.originalRequestDate = originalRequestDate;
        this.vreURL = new StringBuffer(gatewayURL)
                .append(GCubePortalConstants.PREFIX_GROUP_URL)
                .append("/").append(theRequestedVRE.getGroupName().toLowerCase()).toString();
    }

    @Override
    public String compile(String templateContent) {
        String userProfileLink = new StringBuffer(vreURL)
                .append("/").append(getUserProfileLink(theManagerUser.getUsername())).toString();

        return new String(Base64.getDecoder().decode(templateContent))
                .replace("{{REQUESTING_USER_FIRST_NAME}}", theRequestingUser.getFirstName())
                .replace("{{GATEWAY_LOGO:URL}}", getGatewayLogoURL())
                .replace("{{GATEWAY_NAME}}", getGatewayName())
                .replace("{{USER_FULLNAME}}", theManagerUser.getFullname())
                .replace("{{SELECTED_VRE_NAME}}", theRequestedVRE.getGroupName())
                .replace("{{VRE_URL}}", vreURL)
                .replace("{{REQUESTING_USER_EMAIL}}", theRequestingUser.getEmail())
                .replace("{{MANAGE_REQUEST_DATE}}", originalRequestDate.toString())
                .replace("{{VISIBILITY_OF_NEW_ROLES_SECTION}}", this.displayVisibilityOfRolesSectionBasedOnRolesSize(newRoles))
                .replace("{{NEW_ROLES}}", this.getRoleNamesFromRoles(newRoles))
                .replace("{{USER_VREMEMBER_PROFILE_URL}}", userProfileLink)
                .replace("{{VISIBILITY_OF_REVOKED_ROLES_SECTION}}", this.displayVisibilityOfRolesSectionBasedOnRolesSize(rolesRevoked))
                .replace("{{REVOKED_ROLES}}", this.getRoleNamesFromRoles(rolesRevoked));
    }

    @Override
    public String getTextHTML()  {
        this.formatType = FormatType.HTML;
        return compile(encodedTemplateHTML);
    }

    @Override
    public String getTextPLAIN() {
        this.formatType = FormatType.TEXT;
        return compile( this.buildTextPlainBasedOnRoles() );
    }

    private String buildTextPlainBasedOnRoles() {
        StringBuilder finalTextBuilder = new StringBuilder(encodedTemplateTEXTGreeting);

        if(!this.newRoles.isEmpty())
            finalTextBuilder.append(encodedTemplateTEXTAssignedRoles);

        if(!this.rolesRevoked.isEmpty())
            finalTextBuilder.append(encodedTemplateTEXTRevokedRoles);

        return finalTextBuilder.toString();
    }

    private String getUserProfileLink(String username) {
        return "profile?"+ new String(
                Base64.getEncoder().encodeToString(Constants.USER_PROFILE_OID.getBytes())+
                        "="+
                        new String( Base64.getEncoder().encodeToString(username.getBytes()) )
        );
    }

    private String getRoleNamesFromRoles(Collection<GCubeRole> roles) {
        if(this.formatType == FormatType.HTML)
            return this.buildRoleSectionForHTMLFormat(roles);

        if(this.formatType == FormatType.TEXT)
            return this.buildRoleSectionForTextFormat(roles);

        return "";
    }

    private String buildRoleSectionForTextFormat(Collection<GCubeRole> roles) {
        StringBuilder sb = new StringBuilder();
        if(roles == null || roles.isEmpty())
            return sb.append("-").toString();

        boolean first = true;
        for(GCubeRole role : roles) {
            StringBuilder prefix = first ? new StringBuilder() : new StringBuilder(", ");
            sb.append(prefix);
            sb.append(role.getRoleName());
            first = false;
        }

        return sb.toString();
    }

    private String buildRoleSectionForHTMLFormat(Collection<GCubeRole> roles) {
        if(roles == null)
            return "<br> -";

        StringBuilder sb = new StringBuilder();
        sb.append("<ul>");
        roles.forEach(r -> sb.append("<li>" + r.getRoleName() + "</li>"));
        sb.append("</ul>");

        return sb.toString();
    }

    private String displayVisibilityOfRolesSectionBasedOnRolesSize(Collection<GCubeRole> roles) {
        return roles == null || roles.isEmpty() ? this.hiddenSection : this.visibleSection;
    }

}