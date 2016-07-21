package com.accordhk.SnapNEat.models;

import com.google.gson.annotations.SerializedName;

import java.util.List;
import java.util.Map;

/**
 * Created by jm on 21/1/16.
 */
public class PushNotificationPayload {

    /**
     * {"aps":
            {"alert":
                {
                    "body":<message>
                },
                "badge":<notif_count>,
                "content-available:1
            },
            "acme":
            {
                "type":<notif_type>,
                "details":<other_payload>
            }
     }
     */

    private Alert alert;
    private Acme acme;

    public Alert getAlert() {
        return alert;
    }

    public void setAlert(Alert alert) {
        this.alert = alert;
    }

    public Acme getAcme() {
        return acme;
    }

    public void setAcme(Acme acme) {
        this.acme = acme;
    }

    public class Alert {
        @SerializedName("0")
        private Map<String, String> body;
        private int badge;
        @SerializedName("content-available")
        private int content_available;

        public Map<String, String> getBody() {
            return body;
        }

        public void setBody(Map<String, String> body) {
            this.body = body;
        }

        public int getBadge() {
            return badge;
        }

        public void setBadge(int badge) {
            this.badge = badge;
        }

        public int getContent_available() {
            return content_available;
        }

        public void setContent_available(int content_available) {
            this.content_available = content_available;
        }
    }
    public class Acme {
        private Object details;
        private int type;

        public Object getDetails() {
            return details;
        }

        public void setDetails(Object details) {
            this.details = details;
        }

        public int getType() {
            return type;
        }

        public void setType(int type) {
            this.type = type;
        }
    }
}
