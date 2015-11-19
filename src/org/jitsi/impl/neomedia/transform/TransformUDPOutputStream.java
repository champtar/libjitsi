/*
 * Copyright @ 2015 Atlassian Pty Ltd
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.jitsi.impl.neomedia.transform;

import java.net.*;

import org.jitsi.impl.neomedia.*;

/**
 * Extends <tt>RTPConnectorOutputStream</tt> with transform logic for UDP.
 *
 * In this implementation, UDP socket is used to send the data out. When a
 * normal RTP/RTCP packet is passed down from RTPManager, we first transform
 * the packet using user define PacketTransformer and then send it out through
 * network to all the stream targets.
 *
 * @author Bing SU (nova.su@gmail.com)
 * @author Lubomir Marinov
 */
public class TransformUDPOutputStream
    extends RTPConnectorUDPOutputStream
{
    /**
     * The <tt>PacketTransformer</tt> used to transform RTP/RTCP packets.
     */
    private PacketTransformer transformer;

    /**
     * Initializes a new <tt>TransformOutputStream</tt> which is to send packet
     * data out through a specific UDP socket.
     *
     * @param socket the UDP socket used to send packet data out
     */
    public TransformUDPOutputStream(DatagramSocket socket)
    {
        super(socket);
    }

    /**
     * Creates a new array of <tt>RawPacket</tt> from a specific <tt>byte[]</tt>
     * buffer in order to have this instance send its packet data through its
     * {@link #write(byte[], int, int)} method. Transforms the array of packets
     * using a <tt>PacketTransformer</tt>.
     *
     * @param buffer the packet data to be sent to the targets of this instance.
     * The contents of {@code buffer} starting at {@code offset} with the
     * specified {@code length} is copied into the buffer of the returned
     * {@code RawPacket}.
     * @param offset the offset of the packet data in <tt>buffer</tt>
     * @param length the length of the packet data in <tt>buffer</tt>
     * @return a new <tt>RawPacket</tt> containing the packet data of the
     * specified <tt>byte[]</tt> buffer or possibly its modification;
     * <tt>null</tt> to ignore the packet data of the specified <tt>byte[]</tt>
     * buffer and not send it to the targets of this instance through its
     * {@link #write(byte[], int, int)} method
     * @see RTPConnectorOutputStream#createRawPacket(byte[], int, int)
     */
    @Override
    protected RawPacket[] createRawPacket(byte[] buffer, int offset, int length)
    {
        RawPacket[] pkts = super.createRawPacket(buffer, offset, length);
        PacketTransformer transformer = getTransformer();

        if (transformer != null)
        {
            pkts = transformer.transform(pkts);

            /*
             * XXX Allow transformer to abort the writing of buffer by not
             * throwing a NullPointerException if pkt becomes null after
             * transform.
             */
        }
        return pkts;
    }

    /**
     * Gets the <tt>PacketTransformer</tt> which is used to transform packets.
     *
     * @return the <tt>PacketTransformer</tt> which is used to transform packets
     */
    public PacketTransformer getTransformer()
    {
        return transformer;
    }

    /**
     * Sets the <tt>PacketTransformer</tt> which is to be used to transform
     * packets.
     *
     * @param transformer the <tt>PacketTransformer</tt> which is to be used to
     * transform packets
     */
    public void setTransformer(PacketTransformer transformer)
    {
        this.transformer = transformer;
    }
}
