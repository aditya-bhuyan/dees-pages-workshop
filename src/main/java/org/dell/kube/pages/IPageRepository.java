package org.dell.kube.pages;

import java.util.List;

public interface IPageRepository {
    public Page create(Page page);
    public Page read(long id);
    public List<Page> list();
    public Page update(Page page, long id);
    public void delete(long id);
}