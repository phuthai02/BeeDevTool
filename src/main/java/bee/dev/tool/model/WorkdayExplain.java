package bee.dev.tool.model;

import lombok.Data;

@Data
public class WorkdayExplain {
    private String staffCode; // Mã nhân viên
    private String staffName; // Tên nhân viên
    private String staffPart; // Bộ phận
    private String explainDay; // Ngày
    private String shift; // Ca
    private String shiftCode; // Mã ca
    private String workDayCode; // Công
    private String timeIn; // Giờ vào
    private String timeOut; // Giờ ra
    private String timeCount; // Số giờ
    private String lateMinutes; // Số phút đi muộn
    private String earlyMinutes; // Số phút về sớm
    private String reason; // Lý do
    private String note; // Ghi chú
}
