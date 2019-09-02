package com.zd.jraft.node;

import com.zd.jraft.common.Copiable;
import com.zd.jraft.error.RaftError;

public class Status implements Copiable<Status> {

    private State state;

    public Status() {
        this.state = null;
    }

    public Status(int code, String errorMsg) {
        this.state = new State(code, errorMsg);
    }

    public Status(int code, String fmt, Object... args) {
        this.state = new State(code, String.format(fmt, args));
    }

    public Status(RaftError raftError, String fmt, Object... args) {
        this.state = new State(raftError.getNumber(), String.format(fmt, args));
    }

    @Override
    public Status copy() {
        return null;
    }

    private static class State {
        /** error code */
        int    code;
        /** error msg*/
        String msg;

        State(int code, String msg) {
            super();
            this.code = code;
            this.msg = msg;
        }

        @Override
        public int hashCode() {
            final int prime = 31;
            int result = 1;
            result = prime * result + this.code;
            result = prime * result + (this.msg == null ? 0 : this.msg.hashCode());
            return result;
        }

        @Override
        public boolean equals(Object obj) {
            if (this == obj) {
                return true;
            }
            if (obj == null) {
                return false;
            }
            if (getClass() != obj.getClass()) {
                return false;
            }
            State other = (State) obj;
            if (this.code != other.code) {
                return false;
            }
            if (this.msg == null) {
                return other.msg == null;
            } else {
                return this.msg.equals(other.msg);
            }
        }

    }

    public void setError(int code, String fmt, Object... args) {
        this.state = new State(code, String.format(fmt, args));
    }

    public void setError(RaftError error, String fmt, Object... args) {
        this.state = new State(error.getNumber(), String.format(fmt, args));
    }
}
