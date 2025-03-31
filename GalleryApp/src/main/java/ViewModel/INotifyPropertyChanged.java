package ViewModel;

import java.util.ArrayList;
import java.util.List;

public interface INotifyPropertyChanged
{
    void addPropertyChangedListener(PropertyChangedListener listener);
    void removePropertyChangedListener(PropertyChangedListener listener);
    void firePropertyChanged(String propertyName);
}

