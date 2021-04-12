package pl.edu.pwr.lab2.i238162;

public class AudioPreviewBeforeSave extends VideoPreviewBeforeSave{
    public AudioPreviewBeforeSave()
    {
        saveDirectory = R.string.audio_directory;
        saveFailMessage = R.string.audio_save_fail_message;
        savedText = R.string.audio_saved_text;
        filenamePromptDefault = R.string.filename_audio_prompt_default;
        filenameExtension = R.string.filename_audio_extension;
    }
}
