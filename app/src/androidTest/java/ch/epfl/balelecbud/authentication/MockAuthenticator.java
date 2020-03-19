package ch.epfl.balelecbud.authentication;

import android.net.Uri;
import android.os.Parcel;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.google.android.gms.internal.firebase_auth.zzff;
import com.google.firebase.FirebaseApp;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.FirebaseUserMetadata;
import com.google.firebase.auth.UserInfo;
import com.google.firebase.auth.zzy;
import com.google.firebase.auth.zzz;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import ch.epfl.balelecbud.util.Callback;

public class MockAuthenticator implements Authenticator {

    private static final Authenticator instance = new MockAuthenticator();

    private boolean loggedIn;

    private final Map<String, String> users = new HashMap<String, String>() {
        {
            put("karim@epfl.ch", "123456");
        }
    };

    private MockAuthenticator() {
        loggedIn = false;
    }

    @Override
    public void signIn(final String email, final String password, Callback callback) {
        if (users.containsKey(email) && users.get(email).equals(password)) {
            setLoggedIn(true);
            callback.onSuccess();
        } else {
            callback.onFailure("Login failed");
        }
    }

    @Override
    public void createAccount(final String email, final String password, Callback callback) {
        if (!users.containsKey(email)) {
            users.put(email, password);
            setLoggedIn(true);
            callback.onSuccess();
        } else {
            callback.onFailure("Registration failed: account already exists with this email");
        }
    }

    @Override
    public void signOut() {
        setLoggedIn(false);
    }

    @Override
    public FirebaseUser getCurrentUser() {
        if (loggedIn) {
            return new FirebaseUser() {
                @NonNull
                @Override
                public String getUid() {
                    return null;
                }

                @NonNull
                @Override
                public String getProviderId() {
                    return null;
                }

                @Override
                public boolean isAnonymous() {
                    return false;
                }

                @Nullable
                @Override
                public List<String> zza() {
                    return null;
                }

                @NonNull
                @Override
                public List<? extends UserInfo> getProviderData() {
                    return null;
                }

                @NonNull
                @Override
                public FirebaseUser zza(@NonNull List<? extends UserInfo> list) {
                    return null;
                }

                @Override
                public FirebaseUser zzb() {
                    return null;
                }

                @NonNull
                @Override
                public FirebaseApp zzc() {
                    return null;
                }


                @Nullable
                @Override
                public String getDisplayName() {
                    return null;
                }

                @Nullable
                @Override
                public Uri getPhotoUrl() {
                    return null;
                }

                @Nullable
                @Override
                public String getEmail() {
                    return null;
                }

                @Nullable
                @Override
                public String getPhoneNumber() {
                    return null;
                }

                @Nullable
                @Override
                public String zzd() {
                    return null;
                }

                @NonNull
                @Override
                public zzff zze() {
                    return null;
                }

                @Override
                public void zza(@NonNull zzff zzff) {

                }

                @NonNull
                @Override
                public String zzf() {
                    return null;
                }

                @NonNull
                @Override
                public String zzg() {
                    return null;
                }

                @Nullable
                @Override
                public FirebaseUserMetadata getMetadata() {
                    return null;
                }

                @NonNull
                @Override
                public zzz zzh() {
                    return null;
                }

                @Override
                public void zzb(List<zzy> list) {

                }

                @Override
                public void writeToParcel(Parcel dest, int flags) {

                }

                @Override
                public boolean isEmailVerified() {
                    return false;
                }
            };
        }
        return null;
    }

    private void setLoggedIn(boolean state) {
        loggedIn = state;
    }

    public static Authenticator getInstance() {
        return instance;
    }


}
