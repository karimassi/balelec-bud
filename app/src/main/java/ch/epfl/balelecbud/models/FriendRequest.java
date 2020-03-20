package ch.epfl.balelecbud.models;

import java.util.Objects;

public class FriendRequest {

    private String senderId;
    private String recipientId;
    private int status;

    public FriendRequest(){}

    public FriendRequest(String senderId, String recipientId, int status) {
        if (senderId.equals(recipientId)) {
            throw new IllegalArgumentException("The sender and the recipient must be different");
        }
        this.senderId = senderId;
        this.recipientId = recipientId;
        this.status = status;
    }

    public String getSenderId() {
        return senderId;
    }

    public String getRecipientId() {
        return recipientId;
    }

    public int getStatus() {
        return status;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        FriendRequest request = (FriendRequest) o;
        return getStatus() == request.getStatus() &&
                getSenderId().equals(request.getSenderId()) &&
                getRecipientId().equals(request.getRecipientId());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getSenderId(), getRecipientId(), getStatus());
    }
}
