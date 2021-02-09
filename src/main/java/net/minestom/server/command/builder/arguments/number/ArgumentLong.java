package net.minestom.server.command.builder.arguments.number;

import net.minestom.server.MinecraftServer;
import net.minestom.server.command.builder.exception.ArgumentSyntaxException;
import net.minestom.server.network.packet.server.play.DeclareCommandsPacket;
import org.jetbrains.annotations.NotNull;

public class ArgumentLong extends ArgumentNumber<Long> {

    public ArgumentLong(String id) {
        super(id);
        this.min = Long.MIN_VALUE;
        this.max = Long.MAX_VALUE;
    }

    @NotNull
    @Override
    public Long parse(@NotNull String input) throws ArgumentSyntaxException {
        try {
            final long value = Long.parseLong(parseValue(input), getRadix(input));

            // Check range
            if (hasMin && value < min) {
                throw new ArgumentSyntaxException("Input is lower than the minimum required value", input, RANGE_ERROR);
            }
            if (hasMax && value > max) {
                throw new ArgumentSyntaxException("Input is higher than the minimum required value", input, RANGE_ERROR);
            }

            return value;
        } catch (NumberFormatException | NullPointerException e) {
            throw new ArgumentSyntaxException("Input is not a number/long", input, NOT_NUMBER_ERROR);
        }
    }

    @NotNull
    @Override
    public DeclareCommandsPacket.Node[] toNodes(boolean executable) {
        DeclareCommandsPacket.Node argumentNode = simpleArgumentNode(this, executable, false);

        // TODO maybe use ArgumentLiteral/ArgumentWord and impose long restriction server side?

        argumentNode.parser = "brigadier:int";
        argumentNode.properties = packetWriter -> {
            packetWriter.writeByte(MinecraftServer.getCommandManager().getNumberProperties(this));
            if (this.hasMin())
                packetWriter.writeInt(this.getMin().intValue());
            if (this.hasMax())
                packetWriter.writeInt(this.getMax().intValue());
        };

        return new DeclareCommandsPacket.Node[]{argumentNode};
    }

}
