package ch.epfl.balelecbud.utility.connectivity;

public class MockConnectivityChecker implements ConnectivityChecker {

    private boolean available;

    public void setAvailable(boolean available) {
        this.available = available;
    }

    @Override
    public boolean isConnectionAvailable() {
        return available;
    }
}
