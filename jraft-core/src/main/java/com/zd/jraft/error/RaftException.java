package com.zd.jraft.error;

import com.zd.jraft.entity.EnumOuter;
import com.zd.jraft.node.Status;


public class RaftException extends Throwable {

    private EnumOuter.ErrorType type;

    private Status status = Status.OK();

    public RaftException() {
        this.type = EnumOuter.ErrorType.ERROR_TYPE_NONE;
        this.status = Status.OK();
    }

    public RaftException(EnumOuter.ErrorType type) {
        super(type.name());
        this.type = type;
    }

    public RaftException(EnumOuter.ErrorType type, Status status) {
        super(status != null ? status.getErrorMsg() : type.name());
        this.type = type;
        this.status = status;
    }

    public Status getStatus() {
        return this.status;
    }

    public void setStatus(Status status) {
        this.status = status;
    }

    public EnumOuter.ErrorType getType() {
        return this.type;
    }

    public void setType(EnumOuter.ErrorType type) {
        this.type = type;
    }

    @Override
    public String toString() {
        return "Error [type=" + this.type + ", status=" + this.status + "]";
    }
}
