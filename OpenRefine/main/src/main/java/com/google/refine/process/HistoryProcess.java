/*

Copyright 2010, Google Inc.
All rights reserved.

Redistribution and use in source and binary forms, with or without
modification, are permitted provided that the following conditions are
met:

    * Redistributions of source code must retain the above copyright
notice, this list of conditions and the following disclaimer.
    * Redistributions in binary form must reproduce the above
copyright notice, this list of conditions and the following disclaimer
in the documentation and/or other materials provided with the
distribution.
    * Neither the name of Google Inc. nor the names of its
contributors may be used to endorse or promote products derived from
this software without specific prior written permission.

THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS
"AS IS" AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT
LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR
A PARTICULAR PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT
OWNER OR CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL,
SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT
LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE,           
DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY           
THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
(INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE
OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.

*/

package com.google.refine.process;

import java.util.Properties;

import org.json.JSONException;
import org.json.JSONWriter;

import com.google.refine.model.Project;


/**
 * The process for undoing or redoing. This involves calling apply() and revert()
 * on changes.
 */
public class HistoryProcess extends Process {
    final protected Project _project;
    final protected long    _lastDoneID;
    final protected String  _description;
    
    protected boolean _done = false;

    private final static String WARN = "Not a long-running process";
    
    public HistoryProcess(Project project, long lastDoneID) {
        _project = project;
        _lastDoneID = lastDoneID;
        
        if (_lastDoneID == 0) {
            _description = "Undo all";
        } else {
            HistoryEntry entry = _project.history.getEntry(_lastDoneID);
            _description = "Undo/redo until after " + entry.description;
        }
    }
    
    @Override
    public void cancel() {
        throw new RuntimeException(WARN);
    }

    @Override
    public boolean isImmediate() {
        return true;
    }

    @Override
    public HistoryEntry performImmediate() {
        _project.history.undoRedo(_lastDoneID);
        _done = true;
        
        return null;
    }

    @Override
    public void startPerforming(ProcessManager manager) {
        throw new RuntimeException(WARN);
    }

    @Override
    public void write(JSONWriter writer, Properties options)
            throws JSONException {
        
        writer.object();
        writer.key("description"); writer.value(_description);
        writer.key("immediate"); writer.value(true);
        writer.key("status"); writer.value(_done ? "done" : "pending");
        writer.endObject();
    }

    @Override
    public boolean isDone() {
        throw new RuntimeException(WARN);
    }

    @Override
    public boolean isRunning() {
        throw new RuntimeException(WARN);
    }
}
