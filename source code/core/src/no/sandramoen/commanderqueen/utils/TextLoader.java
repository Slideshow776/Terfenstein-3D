package no.sandramoen.commanderqueen.utils;

import com.badlogic.gdx.assets.AssetDescriptor;
import com.badlogic.gdx.assets.AssetLoaderParameters;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.assets.loaders.AsynchronousAssetLoader;
import com.badlogic.gdx.assets.loaders.FileHandleResolver;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.utils.Array;

public class TextLoader extends AsynchronousAssetLoader<Text, TextLoader.TextParameter> {

    public TextLoader(FileHandleResolver resolver) {

        super(resolver);

    }

    Text text;

    @Override
    public void loadAsync(AssetManager manager, String fileName, FileHandle file, TextParameter parameter) {

        this.text = null;
        this.text = new Text(file);

    }

    @Override
    public Text loadSync(AssetManager manager, String fileName, FileHandle file, TextParameter parameter) {

        Text text = this.text;
        this.text = null;

        return text;

    }

    @SuppressWarnings("rawtypes")
    @Override
    public Array<AssetDescriptor> getDependencies(String fileName, FileHandle file, TextParameter parameter) {

        return null;

    }

    public static class TextParameter extends AssetLoaderParameters<Text> {

    }

}