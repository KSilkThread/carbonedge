#
# Copyright Greg Haskins All Rights Reserved.
#
# SPDX-License-Identifier: Apache-2.0
#
FROM arm64/debian:buster
COPY scripts /tmp/scripts
RUN cd /tmp/scripts && \
    common/init.sh && \
    docker/init.sh && \
    common/cleanup.sh && \
    rm -rf /tmp/scripts