// SPDX-License-Identifier: LGPL-2.1-or-later
// Copyright (c) 2012-2014 Monty Program Ab
// Copyright (c) 2015-2021 MariaDB Corporation Ab
// Copyright (c) 2021 SingleStore, Inc.

package com.singlestore.jdbc.pool;

public interface PoolMBean {

  long getActiveConnections();

  long getTotalConnections();

  long getIdleConnections();

  long getConnectionRequests();
}
