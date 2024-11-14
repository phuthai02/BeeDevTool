package bee.dev.tool.model;

import lombok.Data;
import org.apache.poi.ss.usermodel.HorizontalAlignment;
import org.apache.poi.ss.usermodel.VerticalAlignment;
import org.apache.poi.ss.util.CellRangeAddress;

import java.awt.*;

@Data
public class CellStyleConfig {
    private Color backgroundColor;
    private Short textColor;
    private HorizontalAlignment horizontalAlignment;
    private VerticalAlignment verticalAlignment;
    private Boolean isWrapText;
    private Boolean isBold;
    private Boolean isItalic;
    private CellRangeAddress mergedRegion;
}
