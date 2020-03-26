package ch.epfl.balelecbud.models;

import java.util.Objects;

public class FriendRequest {

    private String senderId;
    private String recipientId;

    public FriendRequest(){}

    public FriendRequest(String senderId, String recipientId) {
        if (senderId.equals(recipientId)) {
            throw new IllegalArgumentException("The sender and the recipient must be different");
        }
        this.senderId = senderId;
        this.recipientId = recipientId;
    }

    public String getSenderId() {
        return senderId;
    }

    public String getRecipientId() {
        return recipientId;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        FriendRequest request = (FriendRequest) o;
        return getSenderId().equals(request.getSenderId()) &&
                getRecipientId().equals(request.getRecipientId());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getSenderId(), getRecipientId());
    }
}
