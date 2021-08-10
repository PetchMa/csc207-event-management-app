package gateways;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

// TODO javadocs

public abstract class EntityGateway<T> implements IGateway<T> {
    private final Class<T> dataType;
    private final String path;
    private final Gson gson;
    private Map<String, T> elements;

    public EntityGateway(Class<T> dataType, String path) {
        this.dataType = dataType;
        this.path = path;
        gson = getGsonBuilder().create();
        readElements();
    }

    @Override
    public List<T> getAllElements() {
        return new ArrayList<>(elements.values());
    }

    @Override
    public Map<String, T> getElementMap() {
        return new HashMap<>(elements);
    }

    @Override
    public void saveAllElements(List<T> elements) {
        this.elements = new HashMap<>();
        for (T element: elements)
            this.elements.put(getElementId(element), element);
        writeElements();
    }

    @Override
    public void saveAllElements(Map<String, T> elements) {
        this.elements = new HashMap<>(elements);
        writeElements();
    }

    private void readElements() {
        try {
            FileReader fileReader = new FileReader(path);
            Class<T[]> arrayType = (Class<T[]>) Array.newInstance(dataType, 0).getClass();
            T[] elements = gson.fromJson(fileReader, arrayType);
            fileReader.close();

            this.elements = new HashMap<>();
            for (T element: elements)
                this.elements.put(getElementId(element), element);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void writeElements() {
        try {
            T[] emptyArray = (T[]) Array.newInstance(dataType, 0);
            T[] elements = this.elements.values().toArray(emptyArray);

            FileWriter fileWriter = new FileWriter(path);
            gson.toJson(elements, fileWriter);
            fileWriter.flush();
            fileWriter.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    protected abstract GsonBuilder getGsonBuilder();

    protected abstract String getElementId(T element);
}