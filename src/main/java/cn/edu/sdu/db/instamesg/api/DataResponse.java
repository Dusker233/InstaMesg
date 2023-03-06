package cn.edu.sdu.db.instamesg.api;

public class DataResponse extends ApiResponse {
    protected Object data;

    public DataResponse(Boolean status, String message, Object data) {
        this.status = status;
        this.message = message;
        this.data = data;
    }

    public Object getData() {
        return data;
    }

    public void setData(Object data) {
        this.data = data;
    }
}
