package bookrecommenderdev.server.dao;

import java.sql.PreparedStatement;
import java.sql.SQLException;

@FunctionalInterface
interface Binder { void bind(PreparedStatement ps) throws SQLException; }
