package com.team01.hrbank.storage;

import java.io.InputStream;

public interface BinaryContentStorage {

    Long save(Long id, byte[] bytes);

    InputStream get(Long id);

}
