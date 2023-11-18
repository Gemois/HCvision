package com.hcvision.hcvisionserver.hierarchical.script;

import com.hcvision.hcvisionserver.dataset.Dataset;
import com.hcvision.hcvisionserver.user.User;

public interface PythonScript {
    String getScriptDirName();

    Dataset getDataset();

    User getUser();

    long getId();

}
